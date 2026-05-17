package az.zefflix.gateway.filter;

import az.zefflix.gateway.config.GatewayProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * JWT doğrulama filteri.
 *
 * <p>Hər sorğuda:
 * <ol>
 *   <li>Authorization header-indən Bearer token alınır</li>
 *   <li>Token imzası və müddəti yoxlanılır</li>
 *   <li>Uğurlu olduqda {@code X-User-Id} və {@code X-User-Roles} header-ləri
 *       downstream servisə ötürülür</li>
 *   <li>Public endpoint-lər token olmadan keçir</li>
 * </ol>
 */
@Component
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_ROLES = "roles";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String TYPE_ACCESS = "ACCESS";

    private static final List<String> PUBLIC_PATH_PREFIXES = List.of(
        "/api/v1/auth/",
        "/api/v1/contents",
        "/api/v1/persons/",
        "/api/v1/search/",
        "/actuator/",
        "/v3/api-docs",
        "/swagger-ui"
    );

    private final SecretKey signingKey;

    public JwtAuthenticationFilter(GatewayProperties properties) {
        this.signingKey = Keys.hmacShaKeyFor(
            properties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Public endpoint-lər filterdən keçir
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange, "Authorization header tapilmadi.");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            Claims claims = parseClaims(token);

            // Yalnız ACCESS token qəbul edilir
            if (!TYPE_ACCESS.equals(claims.get(CLAIM_TOKEN_TYPE, String.class))) {
                return unauthorized(exchange, "Yanlish token tipi.");
            }

            String userId = claims.get(CLAIM_USER_ID, String.class);
            String email = claims.getSubject();

            @SuppressWarnings("unchecked")
            List<String> roles = claims.get(CLAIM_ROLES, List.class);

            List<SimpleGrantedAuthority> authorities = (roles == null ? List.<String>of() : roles)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

            // Downstream servisə userId və roller ötürülür
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", userId)
                .header("X-User-Email", email)
                .header("X-User-Roles", String.join(",", roles == null ? List.of() : roles))
                .build();

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, authorities);

            log.debug("JWT auth OK — userId={}, path={}", userId, path);

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (ExpiredJwtException e) {
            log.warn("Token muddeti bitmisdir — path={}", path);
            return unauthorized(exchange, "Token muddeti bitmisdir.");
        } catch (JwtException e) {
            log.warn("Etibarsiz token — path={}, reason={}", path, e.getMessage());
            return unauthorized(exchange, "Etibarsiz token.");
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"success\":false,\"errorCode\":\"UNAUTHORIZED\","
            + "\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(body)));
    }

}

package az.zefflix.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import az.zefflix.gateway.filter.JwtAuthenticationFilter;

/**
 * API Gateway reaktif güvənlik konfiqurasiyası.
 *
 * <p>Gateway WebFlux-da işlədiyi üçün {@code ServerHttpSecurity} istifadə edilir.
 * JWT doğrulaması {@link JwtAuthenticationFilter} tərəfindən aparılır.
 * Spring Security burada yalnız CORS və public endpoint icazələrini idarə edir.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
        "/api/v1/auth/**",
        "/api/v1/contents",
        "/api/v1/contents/**",
        "/api/v1/persons/**",
        "/api/v1/search/**",
        "/actuator/health",
        "/actuator/info",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
        ServerHttpSecurity http,
        JwtAuthenticationFilter jwtFilter) {

        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(PUBLIC_PATHS).permitAll()
                .anyExchange().authenticated()
            )
            // JWT filter runs before Spring Security auth
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

}

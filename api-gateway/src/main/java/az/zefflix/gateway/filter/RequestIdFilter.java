package az.zefflix.gateway.filter;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Hər gələn sorğuya {@code X-Request-Id} header-i əlavə edən filter.
 *
 * <p>Əgər client artıq header göndəribsə, mövcud dəyər saxlanılır.
 * Bu header bütün downstream servislərdə log correlation üçün istifadə olunur.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        final String finalRequestId = requestId;

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
            .header(REQUEST_ID_HEADER, finalRequestId)
            .build();

        log.debug("Request-Id={} method={} path={}",
            finalRequestId,
            exchange.getRequest().getMethod(),
            exchange.getRequest().getPath().value());

        return chain.filter(exchange.mutate().request(mutatedRequest).build())
            .doFinally(signal ->
                exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, finalRequestId));
    }

}

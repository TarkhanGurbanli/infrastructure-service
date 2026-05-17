package az.zefflix.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Bütün gələn sorğuları log edən filter.
 *
 * <p>Log formatı: {@code METHOD /path → STATUS (Xms) [requestId]}
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class LoggingFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getPath().value();
        String requestId = exchange.getRequest().getHeaders().getFirst(REQUEST_ID_HEADER);

        return chain.filter(exchange)
            .doFinally(signal -> {
                ServerHttpResponse response = exchange.getResponse();
                long duration = System.currentTimeMillis() - startTime;
                int status = response.getStatusCode() != null
                    ? response.getStatusCode().value() : 0;

                if (status >= 500) {
                    log.error("{} {} -> {} ({}ms) [{}]", method, path, status, duration, requestId);
                } else if (status >= 400) {
                    log.warn("{} {} -> {} ({}ms) [{}]", method, path, status, duration, requestId);
                } else {
                    log.info("{} {} -> {} ({}ms) [{}]", method, path, status, duration, requestId);
                }
            });
    }

}

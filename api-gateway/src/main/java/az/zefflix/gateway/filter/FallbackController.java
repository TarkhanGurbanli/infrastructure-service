package az.zefflix.gateway.filter;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Circuit breaker açıldıqda bütün servislərin fallback cavabları.
 *
 * <p>Hər route-un {@code fallbackUri} bu controller-ə işarə edir.
 * 503 Service Unavailable qaytarılır ki, client retry apara bilsin.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        log.warn("Circuit breaker OPEN — auth-service əlçatan deyil");
        return fallback("auth-service");
    }

    @GetMapping("/user")
    public Mono<ResponseEntity<Map<String, Object>>> userFallback() {
        log.warn("Circuit breaker OPEN — user-service əlçatan deyil");
        return fallback("user-service");
    }

    @GetMapping("/content")
    public Mono<ResponseEntity<Map<String, Object>>> contentFallback() {
        log.warn("Circuit breaker OPEN — content-service əlçatan deyil");
        return fallback("content-service");
    }

    @GetMapping("/person")
    public Mono<ResponseEntity<Map<String, Object>>> personFallback() {
        log.warn("Circuit breaker OPEN — person-service əlçatan deyil");
        return fallback("person-service");
    }

    @GetMapping("/search")
    public Mono<ResponseEntity<Map<String, Object>>> searchFallback() {
        log.warn("Circuit breaker OPEN — search-service əlçatan deyil");
        return fallback("search-service");
    }

    @GetMapping("/media")
    public Mono<ResponseEntity<Map<String, Object>>> mediaFallback() {
        log.warn("Circuit breaker OPEN — media-service əlçatan deyil");
        return fallback("media-service");
    }

    private Mono<ResponseEntity<Map<String, Object>>> fallback(String serviceName) {
        Map<String, Object> body = Map.of(
            "success", false,
            "errorCode", "SERVICE_UNAVAILABLE",
            "message", serviceName + " hal-hazirda erisilen deyil. Zehmet olmasa sonra yeniden ced edin."
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}

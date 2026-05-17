package az.zefflix.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Zefflix API Gateway.
 *
 * <p>Bütün xarici trafik bu gateway üzərindən keçir.
 * Məsuliyyətlər:
 * <ul>
 *   <li>JWT doğrulaması</li>
 *   <li>Rate limiting (Redis + RequestRateLimiter)</li>
 *   <li>CORS konfiqurasiyası</li>
 *   <li>Request ID header əlavəsi</li>
 *   <li>Circuit breaker (Resilience4j)</li>
 *   <li>Servis routing (Eureka-dan dinamik)</li>
 * </ul>
 *
 * <p>Default port: 8080
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}

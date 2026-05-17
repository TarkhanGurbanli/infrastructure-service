package az.zefflix.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * Redis-əsaslı rate limiting konfiqurasiyası.
 *
 * <p>Üç əsas tier:
 * <ul>
 *   <li>Anonim istifadəçi (IP-ə görə):       30 req/dəq</li>
 *   <li>Autentifika olunmuş (userId-ə görə): 200 req/dəq</li>
 *   <li>Admin:                              1000 req/dəq</li>
 * </ul>
 *
 * <p>{@code /api/v1/auth/login} endpointi {@link LoginRateLimiterConfig}-də
 * ayrıca məhdudlaşdırılır: 5 uğursuz cəhddən sonra 15 dəqiqəlik blok.
 */
@Configuration
public class RateLimiterConfig {

    private final GatewayProperties properties;

    public RateLimiterConfig(GatewayProperties properties) {
        this.properties = properties;
    }

    /**
     * Autentifika olunmuş istifadəçilər üçün rate limiter.
     * Key: X-User-Id header-i və ya IP.
     */
    @Bean
    @Primary
    public RedisRateLimiter authenticatedRateLimiter() {
        int rpm = properties.getRateLimit().getAuthenticatedRequestsPerMinute();
        // replenishRate: saniyədəki token, burstCapacity: ani spike üçün
        return new RedisRateLimiter(rpm / 60 + 1, rpm / 10, 1);
    }

    /**
     * Anonim istifadəçilər üçün rate limiter.
     */
    @Bean
    public RedisRateLimiter anonymousRateLimiter() {
        int rpm = properties.getRateLimit().getAnonymousRequestsPerMinute();
        return new RedisRateLimiter(rpm / 60 + 1, rpm / 10, 1);
    }

    /**
     * Admin-lər üçün rate limiter.
     */
    @Bean
    public RedisRateLimiter adminRateLimiter() {
        int rpm = properties.getRateLimit().getAdminRequestsPerMinute();
        return new RedisRateLimiter(rpm / 60 + 1, rpm / 10, 1);
    }

    /**
     * Rate limiting açarı: autentifika varsa userId, yoxdursa IP ünvanı.
     */
    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId != null && !userId.isBlank()) {
                return Mono.just("user:" + userId);
            }
            // Anonim: IP ünvanı
            return Mono.just("ip:" + exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress());
        };
    }

}

package az.zefflix.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * API Gateway konfiqurasiya xüsusiyyətləri.
 *
 * <p>application.yml-də:
 * <pre>
 * zefflix:
 *   gateway:
 *     jwt-secret: your-secret
 *     rate-limit:
 *       anonymous-requests-per-minute: 30
 *       authenticated-requests-per-minute: 200
 *       admin-requests-per-minute: 1000
 * </pre>
 */
@Validated
@ConfigurationProperties(prefix = "zefflix.gateway")
public class GatewayProperties {

    @NotBlank
    private String jwtSecret;

    private RateLimit rateLimit = new RateLimit();

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public RateLimit getRateLimit() {
        return rateLimit;
    }

    public void setRateLimit(RateLimit rateLimit) {
        this.rateLimit = rateLimit;
    }

    /**
     * Rate limiting konfiqurasiyası.
     */
    public static class RateLimit {

        @Positive
        private int anonymousRequestsPerMinute = 30;

        @Positive
        private int authenticatedRequestsPerMinute = 200;

        @Positive
        private int adminRequestsPerMinute = 1000;

        public int getAnonymousRequestsPerMinute() {
            return anonymousRequestsPerMinute;
        }

        public void setAnonymousRequestsPerMinute(int anonymousRequestsPerMinute) {
            this.anonymousRequestsPerMinute = anonymousRequestsPerMinute;
        }

        public int getAuthenticatedRequestsPerMinute() {
            return authenticatedRequestsPerMinute;
        }

        public void setAuthenticatedRequestsPerMinute(int authenticatedRequestsPerMinute) {
            this.authenticatedRequestsPerMinute = authenticatedRequestsPerMinute;
        }

        public int getAdminRequestsPerMinute() {
            return adminRequestsPerMinute;
        }

        public void setAdminRequestsPerMinute(int adminRequestsPerMinute) {
            this.adminRequestsPerMinute = adminRequestsPerMinute;
        }
    }

}

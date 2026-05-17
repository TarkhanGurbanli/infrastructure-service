package az.zefflix.gateway.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * API Gateway CORS konfiqurasiyası.
 *
 * <p>Bütün downstream servislərin CORS-u buradan idarə edilir —
 * hər servisdə ayrıca CORS konfiqurasiyasına ehtiyac yoxdur.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // İcazə verilən mənşəylər — production-da konkret domenlərlə əvəz et
        config.setAllowedOriginPatterns(List.of(
            "http://localhost:[*]",
            "https://*.zefflix.az"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        config.setAllowedHeaders(List.of(
            "Authorization",
            "Content-Type",
            "X-Request-Id",
            "X-User-Id",
            "Accept",
            "Origin"
        ));

        config.setExposedHeaders(List.of(
            "X-Request-Id",
            "X-Total-Count"
        ));

        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}

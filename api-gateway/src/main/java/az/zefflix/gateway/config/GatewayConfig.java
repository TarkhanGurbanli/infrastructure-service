package az.zefflix.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway ana konfiqurasiya sinifi.
 * {@link GatewayProperties}-i aktivləşdirir.
 */
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class GatewayConfig {

}

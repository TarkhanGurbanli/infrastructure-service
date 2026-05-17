package az.zefflix.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Zefflix Config Server.
 *
 * <p>Bütün mikroservislər konfiqurasiyalarını bu serverdən alır.
 * Git repo-da saxlanılan YAML faylları servis adına görə serv edilir:
 * {@code /{service-name}/{profile}}
 *
 * <p>Default port: 8888
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}

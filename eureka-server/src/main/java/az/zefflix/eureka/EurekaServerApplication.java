package az.zefflix.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Zefflix Eureka Service Discovery Server.
 *
 * <p>Bütün mikroservislər bu serverə qeydiyyatdan keçir
 * və bir-birinin URL-lərini Eureka vasitəsilə tapır.
 *
 * <p>Default port: 8761
 * Dashboard: http://localhost:8761
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}

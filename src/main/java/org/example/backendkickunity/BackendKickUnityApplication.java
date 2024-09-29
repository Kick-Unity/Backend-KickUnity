package org.example.backendkickunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BackendKickUnityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendKickUnityApplication.class, args);
    }

}

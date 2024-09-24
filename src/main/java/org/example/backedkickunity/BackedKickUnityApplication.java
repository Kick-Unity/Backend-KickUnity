package org.example.backedkickunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BackedKickUnityApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackedKickUnityApplication.class, args);
    }

}

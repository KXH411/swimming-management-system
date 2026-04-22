package com.swimming.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.swimming.management")
@EnableJpaRepositories(basePackages = "com.swimming.management.repository")
@EntityScan(basePackages = "com.swimming.management.entity")
public class SwimmingManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(SwimmingManagementApplication.class, args);
    }
}

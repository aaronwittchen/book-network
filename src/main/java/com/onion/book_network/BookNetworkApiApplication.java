package com.onion.book_network;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import com.onion.book_network.role.Role;
import com.onion.book_network.role.RoleRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class BookNetworkApiApplication {

    public static final String ROLE_USER = "USER";

    public static void main(String[] args) {
        SpringApplication.run(BookNetworkApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName(ROLE_USER).isEmpty()) {
                roleRepository.save(Role.builder().name(ROLE_USER).build());
                System.out.println("Initialized USER role");
            }
        };
    }
}

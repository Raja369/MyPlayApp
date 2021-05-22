package org.myplay.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages={"org.myplay"})
@ComponentScan(basePackages={"org.myplay"})
@EntityScan( basePackages = {"org.myplay"} )
public class MyPlayAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyPlayAppApplication.class, args);
	}

}

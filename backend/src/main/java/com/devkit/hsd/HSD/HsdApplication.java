package com.devkit.hsd.HSD;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = {"com.devkit.hsd"})
@EntityScan(basePackages = {"com.devkit.hsd"})
@EnableJpaRepositories(basePackages = {"com.devkit.hsd"})
@SpringBootApplication
public class HsdApplication {

	public static void main(String[] args) {
		SpringApplication.run(HsdApplication.class, args);
	}

}

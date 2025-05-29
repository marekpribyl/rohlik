package com.assignment.rohlik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RohlikApplication {

	public static void main(String[] args) {
		SpringApplication.run(RohlikApplication.class, args);
	}

}

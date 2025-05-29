package com.assignment.rohlik;

import org.springframework.boot.SpringApplication;

public class TestRohlikApplication {

	public static void main(String[] args) {
		SpringApplication.from(RohlikApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

package com.EcoSoftware.Scrum6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableCaching
public class Scrum6Application {


	public static void main(String[] args) {
		SpringApplication.run(Scrum6Application.class, args);
	}

}

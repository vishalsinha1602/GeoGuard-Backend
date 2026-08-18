package com.backend.geosentinel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class
GeosentinelApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeosentinelApplication.class, args);
	}

}





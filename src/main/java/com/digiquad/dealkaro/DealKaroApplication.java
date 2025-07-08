package com.digiquad.dealkaro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true) // Spring Security 6+
public class DealKaroApplication {

	public static void main(String[] args) {
		SpringApplication.run(DealKaroApplication.class, args);
	}

}

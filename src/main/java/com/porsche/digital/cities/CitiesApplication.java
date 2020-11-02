package com.porsche.digital.cities;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@Profile("dev")
public class CitiesApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(CitiesApplication.class, args);
	}
}

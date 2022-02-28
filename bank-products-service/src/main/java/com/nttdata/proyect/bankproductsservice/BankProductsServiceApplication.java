package com.nttdata.proyect.bankproductsservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BankProductsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankProductsServiceApplication.class, args);
	}

}

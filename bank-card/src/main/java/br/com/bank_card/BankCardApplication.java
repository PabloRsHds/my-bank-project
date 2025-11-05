package br.com.bank_card;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BankCardApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankCardApplication.class, args);
	}

}

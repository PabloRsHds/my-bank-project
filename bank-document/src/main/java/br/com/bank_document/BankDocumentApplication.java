package br.com.bank_document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BankDocumentApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankDocumentApplication.class, args);
	}

}

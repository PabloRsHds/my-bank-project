package br.com.bank_notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BankNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankNotificationApplication.class, args);
	}

}

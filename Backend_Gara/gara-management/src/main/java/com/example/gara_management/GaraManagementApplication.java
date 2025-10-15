package com.example.gara_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// import com.example.gara_management.util.EvnLoader;

@SpringBootApplication
public class GaraManagementApplication {

	public static void main(String[] args) {
		// EvnLoader.loadEnv();
		SpringApplication.run(GaraManagementApplication.class, args);
	}

}

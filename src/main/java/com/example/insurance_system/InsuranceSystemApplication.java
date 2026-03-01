package com.example.insurance_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal encargada de inicializar la aplicación Spring Boot del
 * Sistema de Seguros.
 */
@SpringBootApplication
public class InsuranceSystemApplication {

	/**
	 * Método de entrada de la aplicación.
	 *
	 * @param args Argumentos de línea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(InsuranceSystemApplication.class, args);
	}

}

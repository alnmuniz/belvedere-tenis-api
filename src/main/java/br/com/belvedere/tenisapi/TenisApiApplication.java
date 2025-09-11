package br.com.belvedere.tenisapi;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class TenisApiApplication {

	@PostConstruct
    void started() {
        // Garante que a JVM inteira opere em UTC.
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
	public static void main(String[] args) {
		SpringApplication.run(TenisApiApplication.class, args);
	}

}

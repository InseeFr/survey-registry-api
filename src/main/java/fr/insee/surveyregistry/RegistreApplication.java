package fr.insee.surveyregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(scanBasePackages = "fr.insee.surveyregistry")
@ConfigurationPropertiesScan
public class RegistreApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegistreApplication.class, args);
	}
}


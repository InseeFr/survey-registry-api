package registre;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				title = "Registre",
				version = "1.0",
				description = ""
		)
)
@SpringBootApplication
public class RegistreApplication {
	public static void main(String[] args) {
		SpringApplication.run(RegistreApplication.class, args);
	}
}


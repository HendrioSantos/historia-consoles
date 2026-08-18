package historia_consoles.backend_Consoles.aplicacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "historia_consoles.backend_Consoles")
@EnableJpaRepositories(basePackages = "historia_consoles.backend_Consoles")
@EnableJpaAuditing
@EntityScan(basePackages = "historia_consoles.backend_Consoles")
public class BackendConsolesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendConsolesApplication.class, args);
	}

}

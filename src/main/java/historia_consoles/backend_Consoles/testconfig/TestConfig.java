package historia_consoles.backend_Consoles.testconfig;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "historia_consoles.backend_Consoles.core.controller",
        "historia_consoles.backend_Consoles.infra.globalhandler",
        "historia_consoles.backend_Consoles.infra.security",
        "historia_consoles.backend_Consoles.usuario"
})
public class TestConfig {
}

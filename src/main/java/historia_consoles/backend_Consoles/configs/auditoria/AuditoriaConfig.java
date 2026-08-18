package historia_consoles.backend_Consoles.configs.auditoria;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditoriaConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        var autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !autenticacao.isAuthenticated() || autenticacao.getName().equals("anonymousUser")) {
            return Optional.of("SISTEMA/ANÔNIMO");
        }
        return Optional.of(autenticacao.getName());
    }
}

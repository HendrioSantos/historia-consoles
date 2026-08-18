package historia_consoles.backend_Consoles.configs.filtromdc;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.logging.LogRecord;

@Component
public class FiltroUsuarioMdc implements Filter {

    private static final String MDC_USER_KEY = "usuario";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !authentication.getName().equals("anonymousUser")) {
                MDC.put(MDC_USER_KEY, authentication.getName());
            } else {
                MDC.put(MDC_USER_KEY, "anonimo");
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // sempre limpar o mdc para não levar dados em outros lugares
            MDC.remove(MDC_USER_KEY);
        }
    }
}

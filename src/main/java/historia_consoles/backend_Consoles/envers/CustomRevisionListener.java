package historia_consoles.backend_Consoles.envers;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.context.SecurityContextHolder;

public class CustomRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity customEntity = (CustomRevisionEntity) revisionEntity;

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            customEntity.setUsuario(authentication.getName());
        } else {
            customEntity.setUsuario("SISTEMA/ANÔNIMO");
        }
    }
}

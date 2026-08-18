package historia_consoles.backend_Consoles.usuario.exclusao;

import historia_consoles.backend_Consoles.usuario.Usuario;
import historia_consoles.backend_Consoles.usuario.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioExclusaoFisica implements UsuarioExclusao{

    @Override
    public void usuarioExcluir(String login, UsuarioRepository repository) {
        var usuario = repository.findByLogin(login);
        repository.delete((Usuario) usuario);
    }

    @Override
    public boolean exclusaoLogica(boolean logico) {
        return !logico;
    }
}

package historia_consoles.backend_Consoles.usuario.exclusao;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.usuario.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class UsuarioExclusaoLogica implements UsuarioExclusao{

    @Override
    public void usuarioExcluir(String login, UsuarioRepository repository) {
        var usuario = repository.findByLoginAndAtivoTrue(login)
                .orElseThrow(() -> new InvalidoException("Não foi encontrado o usuário"));
        usuario.excluir();
        repository.save(usuario);
    }

    @Override
    public boolean exclusaoLogica(boolean logico) {
        return logico;
    }
}

package historia_consoles.backend_Consoles.usuario.exclusao;

import historia_consoles.backend_Consoles.usuario.UsuarioRepository;

public interface UsuarioExclusao {

    void usuarioExcluir(String login, UsuarioRepository repository);
    boolean exclusaoLogica(boolean logico);
}

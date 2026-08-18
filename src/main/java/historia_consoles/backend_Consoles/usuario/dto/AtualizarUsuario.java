package historia_consoles.backend_Consoles.usuario.dto;

import historia_consoles.backend_Consoles.usuario.Role;
import historia_consoles.backend_Consoles.usuario.Usuario;

public record AtualizarUsuario(
        String login,
        String senha,
        Role role,
        boolean ativo
) {
    public AtualizarUsuario(Usuario usuario) {
        this(
                usuario.getLogin(),
                usuario.getSenha(),
                usuario.getRole(),
                usuario.isAtivo()
        );
    }
}

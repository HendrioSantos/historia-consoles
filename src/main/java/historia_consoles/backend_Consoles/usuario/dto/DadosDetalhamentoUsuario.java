package historia_consoles.backend_Consoles.usuario.dto;

import historia_consoles.backend_Consoles.usuario.Role;
import historia_consoles.backend_Consoles.usuario.Usuario;

public record DadosDetalhamentoUsuario(
        String login,
        Role role,
        boolean ativo
) {
    public DadosDetalhamentoUsuario(Usuario usuario){
        this(
                usuario.getLogin(),
                usuario.getRole(),
                usuario.isAtivo()
        );
    }
}

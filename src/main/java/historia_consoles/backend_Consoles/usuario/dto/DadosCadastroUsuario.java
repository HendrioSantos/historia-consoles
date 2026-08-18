package historia_consoles.backend_Consoles.usuario.dto;

import historia_consoles.backend_Consoles.usuario.Role;

public record DadosCadastroUsuario(
        String login,
        String senha,
        Role role,
        boolean ativo
) {
}

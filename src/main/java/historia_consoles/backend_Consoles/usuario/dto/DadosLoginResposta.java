package historia_consoles.backend_Consoles.usuario.dto;

import historia_consoles.backend_Consoles.usuario.Role;

public record DadosLoginResposta(
        String login,
        String token,
        String mensagem,
        Role role
) {
}

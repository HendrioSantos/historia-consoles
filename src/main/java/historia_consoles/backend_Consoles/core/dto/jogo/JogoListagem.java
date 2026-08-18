package historia_consoles.backend_Consoles.core.dto.jogo;

import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoModo;

public record JogoListagem(
        Long id,
        String nome,
        String desenvolvedora,
        String publicadora,
        String imagemUrl,
        String urlVideo,
        String diretorCriador,
        JogoModo jogoModo,
        JogoGenero jogoGenero,
        Integer notaCritica,
        boolean retrocompatibilidade,
        Long consoleId
) {
    public JogoListagem(Jogo jogo) {
        this(
                jogo.getId(),
                jogo.getNome(),
                jogo.getDesenvolvedora(),
                jogo.getPublicadora(),
                jogo.getImagemUrl(),
                jogo.getUrlVideo(),
                jogo.getDiretorCriador(),
                jogo.getJogoModo(),
                jogo.getJogoGenero(),
                jogo.getNotaCritica(),
                jogo.isAtivo(),
                jogo.getConsole() != null ? jogo.getConsole().getId() : null
        );
    }
}

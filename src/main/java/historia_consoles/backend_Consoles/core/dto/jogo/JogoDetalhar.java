package historia_consoles.backend_Consoles.core.dto.jogo;

import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoMidiaOriginal;
import historia_consoles.backend_Consoles.core.models.enums.JogoStatus;
import historia_consoles.backend_Consoles.core.models.enums.JogoModo;

public record JogoDetalhar(
        Long id,
        String nome,
        String desenvolvedora,
        String publicadora,
        String tamanhoArquivo,
        String imagemUrl,
        String urlVideo,
        String diretorCriador,
        String slug,
        JogoModo jogoModo,
        JogoGenero jogoGenero,
        JogoStatus jogoStatus,
        JogoMidiaOriginal midiaOriginal,
        Integer notaCritica,
        boolean ativo,
        boolean retrocompatibilidade,
        Long consoleId
        ) {
    public JogoDetalhar(Jogo jogo) {
        this(
                jogo.getId(),
                jogo.getNome(),
                jogo.getDesenvolvedora(),
                jogo.getPublicadora(),
                jogo.getTamanhoArquivo(),
                jogo.getImagemUrl(),
                jogo.getUrlVideo(),
                jogo.getDiretorCriador(),
                jogo.getSlug(),
                jogo.getJogoModo(),
                jogo.getJogoGenero(),
                jogo.getJogoStatus(),
                jogo.getMidiaOriginal(),
                jogo.getNotaCritica(),
                jogo.isAtivo(),
                jogo.isRetrocompatibilidade(),
                jogo.getConsole() != null ? jogo.getConsole().getId() : null
        );
    }
}

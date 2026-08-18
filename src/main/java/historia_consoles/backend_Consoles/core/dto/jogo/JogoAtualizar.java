package historia_consoles.backend_Consoles.core.dto.jogo;

import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoMidiaOriginal;
import historia_consoles.backend_Consoles.core.models.enums.JogoStatus;
import historia_consoles.backend_Consoles.core.models.enums.JogoModo;
import jakarta.validation.constraints.Pattern;

public record JogoAtualizar(
        String nome,
        String desenvolvedora,
        String publicadora,
        String tamanhoArquivo,
        @Pattern(regexp = "^(http|https)://.*$", message = "URL inválida")
        String imagemUrl,
        String urlVideo,
        String diretorCriador,
        JogoModo jogoModo,
        JogoGenero jogoGenero,
        JogoStatus jogoStatus,
        JogoMidiaOriginal midiaOriginal,
        Integer notaCritica,
        boolean ativo,
        boolean retrocompatibilidade,
        Long consoleId
) {
    public JogoAtualizar(Jogo jogo) {
        this(
                jogo.getNome(),
                jogo.getDesenvolvedora(),
                jogo.getPublicadora(),
                jogo.getTamanhoArquivo(),
                jogo.getImagemUrl(),
                jogo.getUrlVideo(),
                jogo.getDiretorCriador(),
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

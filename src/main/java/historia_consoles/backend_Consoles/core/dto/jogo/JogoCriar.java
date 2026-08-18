package historia_consoles.backend_Consoles.core.dto.jogo;

import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoMidiaOriginal;
import historia_consoles.backend_Consoles.core.models.enums.JogoStatus;
import historia_consoles.backend_Consoles.core.models.enums.JogoModo;
import jakarta.validation.constraints.*;

public record JogoCriar(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 1, message = "O nome deve ter no máximo 150 caracteres")
        String nome,

        @NotBlank(message = "Desenvolvedora é obrigatório")
        @Size(min = 1, message = "A desenvolvedora deve ter no máximo 150 caracteres")
        String desenvolvedora,

        @NotBlank(message = "Publicadora é obrigatório")
        @Size(min = 1, message = "A publicadora deve ter no máximo 150 caracteres")
        String publicadora,

        @NotBlank(message = "Este é o tamanho de jogo, tipo 64KB, é obrigatório")
        @Size(min = 1, message = "O tamanho do arquivo deve ter no máximo 30 caracteres")
        String tamanhoArquivo,

        @NotNull(message = "Modo jogo é obrigatório")
        JogoModo jogoModo,

        @Pattern(regexp = "^(http|https)://.*$", message = "URL inválida")
        @Size(min = 1, message = "A URL da imagem deve ter no máximo 255 caracteres")
        String imagemUrl,

        @Pattern(regexp = "^https?://.*$", message = "URL do vídeo inválida")
        @Size(max = 255, message = "A URL do vídeo deve ter no máximo 255 caracteres")
        String urlVideo,

        @NotBlank(message = "Diretor criador é obrigatório")
        @Size(max = 150, message = "O diretor criador deve ter no máximo 150 caracteres")
        String diretorCriador,

        @NotNull(message = "O ID do console é obrigatório")
        Long consoleId,

        @NotNull(message = "Gênero do jogo é obrigatório")
        JogoGenero jogoGenero,

        @NotNull(message = "Status do jogo é obrigatório")
        JogoStatus jogoStatus,

        @NotNull(message = "Mídia original do jogo é obrigatória")
        JogoMidiaOriginal midiaOriginal,

        @NotNull(message = "Nota do jogo é obrigatória")
        @PositiveOrZero(message = "A nota deve ser positiva ou zero")
        @Max(value = 100, message = "A nota máxima permitida é 100")
        Integer notaCritica
) {
    public JogoCriar(Jogo jogo) {
        this(
                jogo.getNome(),
                jogo.getDesenvolvedora(),
                jogo.getPublicadora(),
                jogo.getTamanhoArquivo(),
                jogo.getJogoModo(),
                jogo.getImagemUrl(),
                jogo.getUrlVideo(),
                jogo.getDiretorCriador(),
                jogo.getConsole() != null ? jogo.getConsole().getId() : null,
                jogo.getJogoGenero(),
                jogo.getJogoStatus(),
                jogo.getMidiaOriginal(),
                jogo.getNotaCritica()
        );
    }
}

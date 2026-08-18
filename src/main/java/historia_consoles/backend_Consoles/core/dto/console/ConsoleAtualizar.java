package historia_consoles.backend_Consoles.core.dto.console;

import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record ConsoleAtualizar(
        String nome,
        String fabricante,
        String publicadora,
        String unidadesVendidas,
        @Pattern(regexp = "^(http|https)://.*$", message = "URL inválida")
        String imagemUrl,
        @Valid
        Hardware hardware,
        @Valid
        Periodo periodo,
        boolean descontinuado,
        boolean retrocompatibilidade,
        boolean ativo,
        List<Long> jogosId,
        Long geracaoId
) {
    public ConsoleAtualizar(Console console) {
        this(
                console.getNome(),
                console.getFabricante(),
                console.getPublicadora(),
                console.getUnidadesVendidas(),
                console.getImagemUrl(),
                console.getHardware(),
                console.getPeriodo(),
                console.isDescontinuado(),
                console.isRetrocompatibilidade(),
                console.isAtivo(),
                console.getJogos().stream().map(Jogo::getId).toList(),
                console.getGeracao().getId()
        );
    }
}

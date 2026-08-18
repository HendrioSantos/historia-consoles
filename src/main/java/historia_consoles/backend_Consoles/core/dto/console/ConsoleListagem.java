package historia_consoles.backend_Consoles.core.dto.console;

import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;

public record ConsoleListagem(
        Long id,
        String nome,
        String fabricante,
        String publicadora,
        String imagemUrl,
        String unidadesVendidas,
        Hardware hardware,
        Periodo periodo,
        boolean descontinuado,
        boolean retrocompatibilidade,
        Long geracaoId
        ) {
    public ConsoleListagem(Console console) {
        this(
                console.getId(),
                console.getNome(),
                console.getFabricante(),
                console.getPublicadora(),
                console.getImagemUrl(),
                console.getUnidadesVendidas(),
                console.getHardware(),
                console.getPeriodo(),
                console.isDescontinuado(),
                console.isRetrocompatibilidade(),
                console.getGeracao() != null ? console.getGeracao().getId() : null
                );
    }
}

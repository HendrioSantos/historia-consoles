package historia_consoles.backend_Consoles.core.dto.console;

import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;

public record ConsoleDetalhar(
        Long id,
        String nome,
        String fabricante,
        String publicadora,
        String slug,
        String imagemUrl,
        String unidadesVendidas,
        Hardware hardware,
        Periodo periodo,
        boolean ativo,
        boolean descontinuado,
        boolean retrocompatibilidade,
        Long geracaoId
) {
    public ConsoleDetalhar(Console console) {
        this(console.getId(),
                console.getNome(),
                console.getFabricante(),
                console.getPublicadora(),
                console.getSlug(),
                console.getImagemUrl(),
                console.getUnidadesVendidas(),
                console.getHardware(),
                console.getPeriodo(),
                console.isAtivo(),
                console.isDescontinuado(),
                console.isRetrocompatibilidade(),
                console.getGeracao() != null ? console.getGeracao().getId() : null
        );
    }
}

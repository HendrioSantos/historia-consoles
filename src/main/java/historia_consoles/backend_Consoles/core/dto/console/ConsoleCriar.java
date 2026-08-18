package historia_consoles.backend_Consoles.core.dto.console;

import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.stream.Collectors;

public record ConsoleCriar(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres")
        String nome,
        @NotBlank(message = "Fabricante é obrigatório")
        @Size(min = 1, max = 150, message = "O fabricante deve ter no máximo 150 caracteres")
        String fabricante,
        @NotBlank(message = "Publicadora é obrigatório")
        @Size(min = 1, max = 150, message = "A publicadora deve ter no máximo 150 caracteres")
        String publicadora,
        @Pattern(regexp = "^(http|https)://.*$", message = "URL inválida")
        String imagemUrl,
        @NotBlank(message = "Unidades vendidas é obrigatorio, coloque um texto")
        @Size(min = 1, message = "Minimo de caracteres 1")
        String unidadesVendidas,
        @NotNull(message = "O ID da geração é obrigatório")
        Long geracaoId,
        @Valid
        @NotNull(message = "Os dados de hardware são obrigatórios, mas nem todos os campos dentro são")
        Hardware hardware,
        @Valid
        @NotNull(message = "Os dados de período são obrigatórios, e o campo inicio é obrigatório")
        Periodo periodo,
        @NotNull(message = "A lista dos ids dos jogos não pode ser nula")
        List<Long> jogosIds
) {
    public ConsoleCriar(Console console) {
        this(
                console.getNome(),
                console.getFabricante(),
                console.getPublicadora(),
                console.getImagemUrl(),
                console.getUnidadesVendidas(),
                console.getGeracao() != null ? console.getGeracao().getId() : null,
                console.getHardware(),
                console.getPeriodo(),
                console.getJogos() != null ? console.getJogos().stream().map(Jogo::getId).collect(Collectors.toList()) : List.of()
        );
    }
}

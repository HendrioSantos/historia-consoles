package historia_consoles.backend_Consoles.core.dto.geracao;

import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record GeracaoCriar(
        @NotBlank(message = "O nome da geração é obrigatório")
        @Size(min = 1, max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,
        @NotBlank(message = "Fatos históricos são obrigatórios")
        @Size(min = 1, message = "Minimo de 1 caractere")
        String fatoHistorico,
        @Min(value = 1, message = "O número da geração não pode ser menor que 1")
        @Max(value = 10, message = "O número da geração não pode ser maior que 10")
        int numeroGeracao,
        @NotNull(message = "A cronologia da geração é obrigatória")
        GeracaoCronologia cronologia,
        @NotNull(message = "Empresa dominante é obrigatório")
        GeracaoEmpresaDominante geracaoEmpresaDominante,
        @Valid
        @NotNull(message = "Os dados de período são obrigatórios")
        Periodo periodo
) {
    public GeracaoCriar(Geracao geracao) {
        this(
                geracao.getNome(),
                geracao.getFatoHistorico(),
                geracao.getNumeroGeracao(),
                geracao.getGeracaoCronologia(),
                geracao.getGeracaoEmpresaDominante(),
                geracao.getPeriodo()
        );
    }
}

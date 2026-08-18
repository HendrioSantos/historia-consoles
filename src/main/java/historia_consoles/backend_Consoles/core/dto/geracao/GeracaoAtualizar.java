package historia_consoles.backend_Consoles.core.dto.geracao;

import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record GeracaoAtualizar(
        String nome,
        @Min(value = 1, message = "O número da geração não pode ser menor que 1")
        @Max(value = 10, message = "O número da geração não pode ser maior que 10")
        int numeroGeracao,
        String fatoHistorico,
        GeracaoCronologia cronologia,
        GeracaoEmpresaDominante geracaoEmpresaDominante,
        @Valid
        Periodo periodo,
        boolean atual,
        boolean ativo
) {
    public GeracaoAtualizar(Geracao geracao) {
        this(
                geracao.getNome(),
                geracao.getNumeroGeracao(),
                geracao.getFatoHistorico(),
                geracao.getGeracaoCronologia(),
                geracao.getGeracaoEmpresaDominante(),
                geracao.getPeriodo(),
                geracao.isAtual(),
                geracao.isAtivo()
                );
    }
}

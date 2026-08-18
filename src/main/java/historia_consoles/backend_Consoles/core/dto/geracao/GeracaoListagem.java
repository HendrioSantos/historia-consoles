package historia_consoles.backend_Consoles.core.dto.geracao;

import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;

public record GeracaoListagem(
        Long id,
        int numeroGeracao,
        String nome,
        GeracaoCronologia cronologia,
        boolean atual,
        boolean ativo,
        String slug,
        String fatoHistorico,
        GeracaoEmpresaDominante geracaoEmpresaDominante,
        String empresaDominanteDescricao,
        String cronologiaTitulo,
        String cronologiaBits,
        String cronologiaEras,
        String cronologiaDescricao,
        Periodo periodo
) {
    public GeracaoListagem(Geracao geracao) {
        this(
                geracao.getId(),
                geracao.getNumeroGeracao(),
                geracao.getNome(),
                geracao.getGeracaoCronologia(),
                geracao.isAtual(),
                geracao.isAtivo(),
                geracao.getSlug(),
                geracao.getFatoHistorico(),
                geracao.getGeracaoEmpresaDominante(),
                geracao.getGeracaoEmpresaDominante() != null ? geracao.getGeracaoEmpresaDominante().getDescricao() : null,
                geracao.getGeracaoCronologia() != null ? geracao.getGeracaoCronologia().getTituloExibicao() : null,
                geracao.getGeracaoCronologia() != null ? geracao.getGeracaoCronologia().getCapacidadeBits() : null,
                geracao.getGeracaoCronologia() != null ? geracao.getGeracaoCronologia().getErasCorrespondentes() : null,
                geracao.getGeracaoCronologia() != null ? geracao.getGeracaoCronologia().getDescricaoTecnica() : null,
                geracao.getPeriodo()
        );
    }
}

package historia_consoles.backend_Consoles.core.service;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoAtualizar;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoCriar;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoListagem;
import historia_consoles.backend_Consoles.core.exclusao.geracao.GeracaoExclusao;
import historia_consoles.backend_Consoles.core.models.OrdemGeracao;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeracaoService {

    private final GeracaoRepository repository;
    private final List<GeracaoExclusao> excluir;
    private final FiltradorSlug slugger;

    @Transactional(readOnly = true)
    public Geracao encontrarGeracao(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.warn("Geração com ID {} não encontrada", id);
                    return new InvalidoException("Não encontrei a geração");
                });
    }
    private Geracao buildGeracao(GeracaoCriar dados) {
        if (dados.periodo() != null && !dados.periodo().isPeriodoValido()) {
            throw new InvalidoException("A data de início do período não pode ser posterior à data de fim.");
        }

        return Geracao.builder()
                .nome(dados.nome())
                .fatoHistorico(dados.fatoHistorico())
                .numeroGeracao(dados.numeroGeracao())
                .geracaoCronologia(dados.cronologia())
                .geracaoEmpresaDominante(dados.geracaoEmpresaDominante())
                .periodo(dados.periodo())
                .slug(FiltradorSlug.gerarSlug(dados.nome()))
                .build();
    }

    @Transactional
    public Geracao criarGeracao(GeracaoCriar dados){
        log.info("Iniciando criação de nova geração: '{}', Número: {}", dados.nome(), dados.numeroGeracao());
        var geracao = buildGeracao(dados);

        if (!geracao.isAtual()){
            repository.findByAtualTrue().ifPresent(geracaoAntiga -> {
                log.info("Removendo status de 'Atual' da geração antiga ID: {}", geracaoAntiga.getId());
                geracaoAntiga.setAtual(false);
                repository.save(geracaoAntiga);
            });
        }
        log.info("Geração '{}' criada com sucesso. ID gerado: {}", geracao.getNome(), geracao.getId());
        return repository.save(geracao);
    }

    @Transactional
    public Geracao atualizarGeracao(Long id, GeracaoAtualizar dados){
        log.info("Iniciando atualização da geração ID: {}", id);
        var geracao = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new InvalidoException("Não encontrei a geração"));
        geracao.atualizarInformacoes(dados, this.slugger);
        log.info("Geração ID {} atualizada com sucesso", id);
        return repository.save(geracao);
    }

    private GeracaoCronologia processarGeracao(Integer valor) {
        log.debug("Processando cronologia para a geração número: {}", valor);
        var geracao = new OrdemGeracao(valor);
        return geracao.obterCronologia();
    }

    public void deletarGeracao(Long id, boolean logico) {
        excluir.stream()
                .filter(e -> e.exclusaoLogica(logico))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Nenhuma estratégia de exclusão encontrada para o tipo lógico: {}", logico);
                    return new InvalidoException("Não encontrei a geração");
                })
                .encontrarGeracao(id, repository);
        log.info("Processo de exclusão executado para ID: {} e logico = {}", id, logico);
    }

    public Page<GeracaoListagem> listarPaginas(Pageable paginacao) {
        log.info("Listando gerações paginadas. Página: {}, Tamanho: {}", paginacao.getPageNumber(), paginacao.getPageSize());
        return repository.findAllByAtivoTrue(paginacao)
                .map(GeracaoListagem::new);
    }

    public Geracao encontrarGeracaoPorSlug(String slug) {
        log.info("Buscando geração pelo slug: '{}'", slug);
        return repository.findBySlugIgnoreCaseAndAtivoTrue(slug)
                .orElseThrow(() -> {
                    log.warn("Geração com slug '{}' não encontrada", slug);
                    return new InvalidoException("Não encontrei a geração");
                });
    }
}

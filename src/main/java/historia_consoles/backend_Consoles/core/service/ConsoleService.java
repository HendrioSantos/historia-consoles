package historia_consoles.backend_Consoles.core.service;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleAtualizar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleCriar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleListagem;
import historia_consoles.backend_Consoles.core.exclusao.console.ConsoleExclusao;
import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import historia_consoles.backend_Consoles.validadores.ValidadorImagemUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsoleService {

    private final ConsoleRepository repository;
    private final GeracaoRepository geracaoRepository;
    private final FiltradorSlug slugger;
    private final ValidadorImagemUrl url;
    private final List<ConsoleExclusao> metodoExclusao;

    @Transactional(readOnly = true)
    public Page<ConsoleListagem> listarPorGeracao(@PathVariable("geracaoId") Long geracaoId,
                                                  @PageableDefault(sort = "id") Pageable paginacao) {
        log.info("Listando consoles da geração ID: {}. Página: {}, Tamanho: {}", geracaoId, paginacao.getPageNumber(), paginacao.getPageSize());
        return repository.findAllByGeracaoIdAndAtivoTrue(geracaoId, paginacao)
                .map(ConsoleListagem::new);
    }

    @Transactional(readOnly = true)
    public Console encontrarConsolePorSlug(String slug) {
        log.info("Buscando console pelo slug: '{}'", slug);
        return repository.findBySlugIgnoreCaseAndAtivoTrue(slug)
                .orElseThrow(() -> {
                    log.warn("Console com slug '{}' não encontrado", slug);
                    return new InvalidoException("Não foi possível encontrar o console pelo slug");
                });
    }

    @Transactional(readOnly = true)
    public Console encontrarConsole(Long id) {
        log.info("Buscando console por ID: {}", id);
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.warn("Console ID {} não encontrado", id);
                    return new InvalidoException("Não encontrei o console");
                });
    }

    @Transactional(readOnly = true)
    public Page<ConsoleListagem> listarPaginas(Pageable paginacao) {
        log.info("Listando todos os consoles ativos. Página: {}, Tamanho: {}", paginacao.getPageNumber(), paginacao.getPageSize());
        return repository.findAllByAtivoTrue(paginacao)
                .map(ConsoleListagem::new);
    }

    @Transactional
    public Console criar(ConsoleCriar dados) {
        log.info("Solicitação para criar console: '{}' (Fabricante: {})", dados.nome(), dados.fabricante());
        var console = buildConsole(dados);
        var salvo = repository.save(console);
        log.info("Console '{}' criado com sucesso. ID gerado: {}", salvo.getNome(), salvo.getId());
        return salvo;
    }

    @Transactional
    public Console atualizar(Long id, ConsoleAtualizar dados) {
        log.info("Iniciando atualização do console ID: {}", id);
        var console = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao atualizar: Console ID {} não existe", id);
                    return new InvalidoException("Console não encontrado");
                });

        String novoSlug = (dados.nome() != null && !dados.nome().isBlank())
                ? FiltradorSlug.gerarSlug(dados.nome())
                : console.getSlug();

        String novaImagem = (dados.imagemUrl() != null && !dados.imagemUrl().isBlank())
                ? this.url.validarImagemUrl(dados.imagemUrl())
                : console.getImagemUrl();

        console.atualizar(dados, novoSlug, novaImagem);
        log.info("Console ID {} atualizado com sucesso. Novo slug: '{}'", id, novoSlug);
        return repository.save(console);
    }

    @Transactional
    public void processarExclusao(Long id, boolean logico){
        log.info("Iniciando exclusão do console ID: {} (Modo lógico: {})", id, logico);
        metodoExclusao.stream()
                .filter(m -> m.exclusaoLogica(logico))
                .findFirst()
                .orElseThrow(() -> new InvalidoException("Metodo de exclusão não suportado"))
                .consoleExcluir(id, repository);
        log.info("Console ID {} processado pelo método de exclusão com sucesso", id);
    }

    private Console buildConsole(ConsoleCriar dados) {
        log.debug("Criando o Console para a geração ID: {}", dados.geracaoId());
        if (dados.periodo() != null && !dados.periodo().isPeriodoValido()) {
            log.warn("Falha na validação: Período com datas inconsistentes para o console '{}'", dados.nome());
            throw new InvalidoException("A data de início do período não pode ser posterior à data de fim.");
        }
        var geracao = geracaoRepository.findById(dados.geracaoId())
                .orElseThrow(() -> {
                    log.warn("Falha na validação: Geração ID {} não foi encontrada", dados.geracaoId());
                    return new InvalidoException("Geração não encontrada");
                });
        var imagem = (dados.imagemUrl() != null && !dados.imagemUrl().isBlank())
                ? this.url.validarImagemUrl(dados.imagemUrl())
                : null;

        return Console.builder()
                .nome(dados.nome())
                .fabricante(dados.fabricante())
                .publicadora(dados.publicadora())
                .unidadesVendidas(dados.unidadesVendidas())
                .geracao(geracao)
                .imagemUrl(url.validarImagemUrl(imagem))
                .slug(FiltradorSlug.gerarSlug(dados.nome()))
                .hardware(Hardware.builder()
                        .cpu(dados.hardware().getCpu())
                        .gpu(dados.hardware().getGpu())
                        .ram(dados.hardware().getRam())
                        .armazenamento(dados.hardware().getArmazenamento())
                        .midia(dados.hardware().getMidia())
                        .resolucao(dados.hardware().getResolucao())
                        .consoleTipo(dados.hardware().getConsoleTipo())
                        .precoLancamento(dados.hardware().getPrecoLancamento())
                        .build())
                .periodo(Periodo.builder()
                        .inicio(dados.periodo().getInicio())
                        .fim(dados.periodo().getFim())
                        .build())
                .build();
    }

}

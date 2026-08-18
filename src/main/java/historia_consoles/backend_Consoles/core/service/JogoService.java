package historia_consoles.backend_Consoles.core.service;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoAtualizar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoCriar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoDetalhar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoListagem;
import historia_consoles.backend_Consoles.core.exclusao.jogo.JogoMetodoExclusao;
import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;
import historia_consoles.backend_Consoles.core.repository.JogoRepository;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import historia_consoles.backend_Consoles.validadores.ValidadorImagemUrl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JogoService {

    private final JogoRepository repository;
    private final ConsoleRepository consoleRepository;
    private final ValidadorImagemUrl imagemUrl;
    private final List<JogoMetodoExclusao> metodoExclusao;
    private final FiltradorSlug sluger;

    @Transactional(readOnly = true)
    public JogoDetalhar encontrarJogoPorSlug(String slug){
        return repository.findBySlugAndAtivoTrue(slug)
                .map(JogoDetalhar::new)
                .orElseThrow(() -> {
                    log.warn("Busca por slug falhou: '{}' não encontrado ou inativo", slug);
                    return new InvalidoException("Não foi possível encontrar o jogo pelo slug");
                });
    }

    @Transactional(readOnly = true)
    public Page<JogoListagem> listarPorConsole(Long consoleId, Pageable paginacao) {
        return repository.findAllByConsoleIdAndAtivoTrue(consoleId, paginacao)
                .map(JogoListagem::new);
    }

    @Transactional(readOnly = true)
    public JogoDetalhar encontrarJogo(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .map(JogoDetalhar::new)
                .orElseThrow(() -> {
                    log.warn("Busca por ID falhou: Jogo ID {} não encontrado", id);
                    return new InvalidoException("Não encontrei o jogo");
                });
    }

    @Transactional(readOnly = true)
    public Page<JogoListagem> listarPaginas(Pageable paginacao){
        return repository.findAllByAtivoTrue(paginacao)
                .map(JogoListagem::new);
    }

    @Transactional
    public Jogo criar(JogoCriar dados){
        log.debug("Iniciando processo de criação do jogo '{}'", dados.nome());
        var jogo = buildJogo(dados);
        var jogoSalvo = repository.save(jogo);
        log.info("Jogo '{}' salvo com sucesso no banco. ID gerado: {}", jogoSalvo.getNome(), jogoSalvo.getId());
        return jogoSalvo;
    }

    @Transactional
    public Jogo atualizar(Long id, JogoAtualizar dados) {
        var jogo = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> {
                    log.warn("Falha ao atualizar: Jogo ID {} não cadastrado", id);
                    return new InvalidoException("Jogo não encontrado");
                });
        jogo.atualizarJogo(dados);

        if (dados.imagemUrl() != null && !dados.imagemUrl().isBlank()) {
            log.debug("Validando nova URL de imagem para o jogo ID: {}", id);
            imagemUrl.validarImagemUrl(dados.imagemUrl());
        }
        log.info("Jogo ID: {} atualizado com sucesso.", id);
        return repository.save(jogo);
    }

    @Transactional
    public void processarExclusao(Long id, boolean logico){
        log.debug("Buscando estratégia válida para exclusão do jogo ID: {}", id);
        metodoExclusao.stream()
                .filter(m -> m.exclusaoLogica(logico))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Erro crítico: Nenhum método mapeado para exclusaoLogica={}", logico);
                    return new InvalidoException("Metodo de exclusão não suportado");
                }).JogoExcluir(id, repository);
        log.info("Processo de exclusão executado para o jogo ID: {}", id);
    }

    private Jogo buildJogo(JogoCriar dados) {
        log.debug("Buscando console ID {} para vincular ao novo jogo", dados.consoleId());
        var console = consoleRepository.findByIdAndAtivoTrue(dados.consoleId())
                .orElseThrow(() -> {
                    log.warn("Falha na criação do jogo: Console ID {} não encontrado", dados.consoleId());
                    return new InvalidoException("Console não encontrado");
                });
        var imagemValidada = (dados.imagemUrl() != null && !dados.imagemUrl().isBlank())
                ? this.imagemUrl.validarImagemUrl(dados.imagemUrl())
                : null;

        log.debug("Gerando slug e montando objeto Jogo para o título: '{}'", dados.nome());
        return Jogo.builder()
                .nome(dados.nome())
                .desenvolvedora(dados.desenvolvedora())
                .publicadora(dados.publicadora())
                .tamanhoArquivo(dados.tamanhoArquivo())
                .jogoModo(dados.jogoModo())
                .imagemUrl(imagemValidada)
                .urlVideo(dados.urlVideo())
                .diretorCriador(dados.diretorCriador())
                .console(console)
                .jogoGenero(dados.jogoGenero())
                .jogoStatus(dados.jogoStatus())
                .midiaOriginal(dados.midiaOriginal())
                .notaCritica(dados.notaCritica())
                .slug(FiltradorSlug.gerarSlug(dados.nome()))
                .build();
    }

}

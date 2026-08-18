package historia_consoles.backendConsoles.core.repository;

import historia_consoles.backend_Consoles.aplicacao.BackendConsolesApplication;
import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.*;
import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import historia_consoles.backend_Consoles.core.repository.JogoRepository;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@ContextConfiguration(classes = BackendConsolesApplication.class)
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("Testando a classe Jogo")
class JogoRepositoryTest {

    @Autowired
    private JogoRepository repository;

    @Autowired
    private ConsoleRepository consoleRepository;

    @Autowired
    private GeracaoRepository geracaoRepository;

    @Test
    @DisplayName("Criando um jogo normal com sucesso")
    void deveBuscarJogoComSucesso(){
        var geracao = Geracao.builder()
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);

        var console = Console.builder()
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console"))
                .unidadesVendidas("100 milhões")
                .build();
        var consoleSalvo = consoleRepository.saveAndFlush(console);

        var jogo = Jogo.builder()
                .nome("Chrono Trigger")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.MULTIPLAYER)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogoSalvo = repository.saveAndFlush(jogo);
        var jogobd = repository.findByIdAndAtivoTrue(jogoSalvo.getId());

        assertTrue(jogobd.isPresent());
        assertEquals(jogoSalvo, jogobd.get(), "seila");
        assertEquals("Chrono Trigger", jogobd.get().getNome());
    }

    @Test
    @DisplayName("Buscando jogos com um genero somente")
    void deveBuscarJogoComGeneroUnico() {
        var geracao = Geracao.builder()
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);

        var console = Console.builder()
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console"))
                .unidadesVendidas("100 milhões")
                .build();
        var consoleSalvo = consoleRepository.saveAndFlush(console);

        var jogo = Jogo.builder()
                .nome("Chrono Trigger")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.MULTIPLAYER)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogo2 = Jogo.builder()
                .nome("Chrono Trigger2")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger2"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogo3 = Jogo.builder()
                .nome("Chrono Trigger3")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger3"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.ESTRATEGIA)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogoSalvo = repository.saveAndFlush(jogo);
        var jogoSalvo2 = repository.saveAndFlush(jogo2);
        var jogoSalvo3 = repository.saveAndFlush(jogo3);

        Pageable pagina = PageRequest.of(0, 10);
        Page<Jogo> paginaJogo = repository.findAllByJogoGenero(jogoSalvo.getJogoGenero(), pagina);

        var jogoBool = paginaJogo.getContent().contains(jogoSalvo);
        var jogoBool2 = paginaJogo.getContent().contains(jogoSalvo2);
        var jogoBool3 = paginaJogo.getContent().contains(jogoSalvo3);

        assertTrue(jogoBool);
        assertTrue(jogoBool2);
        assertFalse(jogoBool3);
    }

    @Test
    @DisplayName("Buscando jogo com um status somente")
    void deveBuscarJogoComStatusUnico() {
        var geracao = Geracao.builder()
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);

        var console = Console.builder()
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console"))
                .unidadesVendidas("100 milhões")
                .build();
        var consoleSalvo = consoleRepository.saveAndFlush(console);

        var jogo = Jogo.builder()
                .nome("Chrono Trigger")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogo2 = Jogo.builder()
                .nome("Chrono Trigger2")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger2"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.FORA_MERCADO)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogo3 = Jogo.builder()
                .nome("Chrono Trigger3")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger3"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.LANCADO)
                .jogoGenero(JogoGenero.ESTRATEGIA)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogoSalvo = repository.saveAndFlush(jogo);
        var jogoSalvo2 = repository.saveAndFlush(jogo2);
        var jogoSalvo3 = repository.saveAndFlush(jogo3);

        Pageable pagina = PageRequest.of(0, 10);
        Page<Jogo> paginaJogo = repository.findAllByConsoleIdAndJogoStatus(consoleSalvo.getId(), JogoStatus.ENTRADA_SISTEMA, pagina);

        var jogoBool = paginaJogo.getContent().contains(jogoSalvo);
        var jogoBool2 = paginaJogo.getContent().contains(jogoSalvo2);
        var jogoBool3 = paginaJogo.getContent().contains(jogoSalvo3);

        assertTrue(jogoBool);
        assertFalse(jogoBool2);
        assertFalse(jogoBool3);
    }

    @Test
    @DisplayName("Buscando jogo com nota critica alta")
    void deveBuscarJogoComNotaCriticaAlta() {
        var geracao = Geracao.builder()
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);

        var console = Console.builder()
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console"))
                .unidadesVendidas("100 milhões")
                .build();
        var consoleSalvo = consoleRepository.saveAndFlush(console);

        var jogo = Jogo.builder()
                .nome("Chrono Trigger")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogo2 = Jogo.builder()
                .nome("Chrono Trigger2")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger2"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.FORA_MERCADO)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(90)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogo3 = Jogo.builder()
                .nome("Chrono Trigger3")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger3"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.LANCADO)
                .jogoGenero(JogoGenero.ESTRATEGIA)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(89)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogoSalvo = repository.saveAndFlush(jogo);
        var jogoSalvo2 = repository.saveAndFlush(jogo2);
        var jogoSalvo3 = repository.saveAndFlush(jogo3);

        Pageable pagina = PageRequest.of(0, 10);
        Page<Jogo> paginaJogo = repository.findAllByNotaCriticaGreaterThanEqual(90, pagina);

        var jogoBool = paginaJogo.getContent().contains(jogoSalvo);
        var jogoBool2 = paginaJogo.getContent().contains(jogoSalvo2);
        var jogoBool3 = paginaJogo.getContent().contains(jogoSalvo3);

        assertTrue(jogoBool);
        assertTrue(jogoBool2);
        assertFalse(jogoBool3);
    }

    @Test
    @DisplayName("Buscando jogo com retrocompatibilidade")
    void deveBuscarJogoComRetrocompatibilidadeTrue() {
        var geracao = Geracao.builder()
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);

        var console = Console.builder()
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console"))
                .unidadesVendidas("100 milhões")
                .build();
        var consoleSalvo = consoleRepository.saveAndFlush(console);

        var jogo = Jogo.builder()
                .nome("Chrono Trigger")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(true)
                .ativo(true)
                .build();

        var jogo2 = Jogo.builder()
                .nome("Chrono Trigger2")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger2"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.FORA_MERCADO)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(90)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(false)
                .ativo(true)
                .build();

        var jogoSalvo = repository.saveAndFlush(jogo);
        var jogoSalvo2 = repository.saveAndFlush(jogo2);

        Pageable pagina = PageRequest.of(0, 10);
        Page<Jogo> paginaJogo = repository.findAllByRetrocompatibilidadeTrue(pagina);

        var jogoBool = paginaJogo.getContent().contains(jogoSalvo);
        var jogoBool2 = paginaJogo.getContent().contains(jogoSalvo2);

        assertTrue(jogoBool);
        assertFalse(jogoBool2);
    }

    @Test
    @DisplayName("Buscando genero de jogo diferente")
    void naoDeveBuscarJogoComGeneroDiferente() {
        var geracao = Geracao.builder()
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeira"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);

        var console = Console.builder()
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console"))
                .unidadesVendidas("100 milhões")
                .build();
        var consoleSalvo = consoleRepository.saveAndFlush(console);

        var jogo = Jogo.builder()
                .nome("Chrono Trigger")
                .desenvolvedora("Square")
                .publicadora("Square")
                .slug(FiltradorSlug.gerarSlug("Chrono Trigger"))
                .console(consoleSalvo)
                .jogoStatus(JogoStatus.ENTRADA_SISTEMA)
                .jogoGenero(JogoGenero.RPG)
                .imagemUrl("http://link-da-imagem-do-jogo.com")
                .urlVideo("https://youtube.com")
                .notaCritica(96)
                .jogoModo(JogoModo.SOLO)
                .diretorCriador("Hironobu Sakaguchi")
                .midiaOriginal(JogoMidiaOriginal.CARTUCHO)
                .tamanhoArquivo("4MB")
                .retrocompatibilidade(true)
                .ativo(true)
                .build();

        var jogoSalvo = repository.saveAndFlush(jogo);

        Pageable pagina = PageRequest.of(0, 10);
        Page<Jogo> paginaJogo = repository.findAllByJogoGenero(JogoGenero.LUTA, pagina);

        assertEquals(0, paginaJogo.getTotalElements());
        assertFalse(paginaJogo.getContent().contains(jogoSalvo));
    }

}
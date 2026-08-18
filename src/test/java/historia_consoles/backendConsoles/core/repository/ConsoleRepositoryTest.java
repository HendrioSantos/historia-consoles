package historia_consoles.backendConsoles.core.repository;

import historia_consoles.backend_Consoles.aplicacao.BackendConsolesApplication;
import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
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

@DataJpaTest
@ContextConfiguration(classes = BackendConsolesApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Testes de console com e sem lista")
class ConsoleRepositoryTest {

    @Autowired
    private ConsoleRepository repository;

    @Autowired
    private GeracaoRepository geracaoRepository;

    @Test
    void deveBuscarConsoleComSucesso(){
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
        var consoleSalvo = repository.saveAndFlush(console);

        var consoleOptional = repository.findByIdAndAtivoTrue(consoleSalvo.getId());
        assertTrue(consoleOptional.isPresent());

        assertEquals(consoleSalvo, consoleOptional.get());
    }

    @Test
    void deveBuscarConsoleComoListaAtiva() {
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

        var consoleAtivo = Console.builder()
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
        repository.saveAndFlush(consoleAtivo);

        var consoleInativo = Console.builder()
                .nome("console1")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(false)
                .slug(FiltradorSlug.gerarSlug("console1"))
                .unidadesVendidas("100 milhões")
                .build();

        repository.saveAndFlush(consoleInativo);

        Pageable paginacao = PageRequest.of(0, 10);
        Page<Console> paginaResultado = repository.findAllByAtivoTrue(paginacao);

        var resultadoDoBanco = paginaResultado.getContent();

        boolean encontrouNossoConsoleAtivo = resultadoDoBanco.contains(consoleAtivo);
        boolean encontrouNossoConsoleInativo = resultadoDoBanco.contains(consoleInativo);

        assertTrue(encontrouNossoConsoleAtivo);
        assertFalse(encontrouNossoConsoleInativo);
    }

    @Test
    void deveRetornarGeracaoComConsoleAtivo(){
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

        var geracao2 = Geracao.builder()
                .nome("Primeiras")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeiras"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();

        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);
        var geracaoSalva2 = geracaoRepository.saveAndFlush(geracao2);

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

        var console2 = Console.builder()
                .nome("console2")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console2"))
                .unidadesVendidas("100 milhões")
                .build();

        var console3 = Console.builder()
                .nome("console3")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva2)
                .imagemUrl("http")
                .ativo(true)
                .slug(FiltradorSlug.gerarSlug("console3"))
                .unidadesVendidas("100 milhões")
                .build();

        repository.saveAndFlush(console);
        repository.saveAndFlush(console2);
        repository.saveAndFlush(console3);

        Pageable paginacao = PageRequest.of(0, 10);
        Page<Console> paginaResultado = repository.findAllByGeracaoIdAndAtivoTrue(geracao.getId(), paginacao);

        var pagina = paginaResultado.getContent();
        var pConsole = pagina.contains(console);
        var pConsole2 = pagina.contains(console2);
        var pConsole3 = pagina.contains(console3);

        assertTrue(pConsole);
        assertTrue(pConsole2);
        assertFalse(pConsole3);
    }

    @Test
    void deveRetornarGeracaoComConsoleInativo(){
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

        var geracao2 = Geracao.builder()
                .nome("Primeiras")
                .numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true)
                .atual(false)
                .slug(FiltradorSlug.gerarSlug("Primeiras"))
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();

        var geracaoSalva = geracaoRepository.saveAndFlush(geracao);
        var geracaoSalva2 = geracaoRepository.saveAndFlush(geracao2);

        var console = Console.builder()
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(false)
                .slug(FiltradorSlug.gerarSlug("console"))
                .unidadesVendidas("100 milhões")
                .build();

        var console2 = Console.builder()
                .nome("console2")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva)
                .imagemUrl("http")
                .ativo(false)
                .slug(FiltradorSlug.gerarSlug("console2"))
                .unidadesVendidas("100 milhões")
                .build();

        var console3 = Console.builder()
                .nome("console3")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracaoSalva2)
                .imagemUrl("http")
                .ativo(false)
                .slug(FiltradorSlug.gerarSlug("console3"))
                .unidadesVendidas("100 milhões")
                .build();

        repository.saveAndFlush(console);
        repository.saveAndFlush(console2);
        repository.saveAndFlush(console3);

        Pageable paginacao = PageRequest.of(0, 10);
        Page<Console> paginaResultado = repository.findAllByAtivoTrue(paginacao);

        var pagina = paginaResultado.getContent();
        var pConsole = pagina.contains(console);
        var pConsole2 = pagina.contains(console2);
        var pConsole3 = pagina.contains(console3);

        assertFalse(pConsole);
        assertFalse(pConsole2);
        assertFalse(pConsole3);
    }

    @Test
    void deveRetornarOptionalVazioQuandoBuscarPorIdInexistente() {
        var idInexistente = 999L;
        var consoleOptional = repository.findByIdAndAtivoTrue(idInexistente);

        assertFalse(consoleOptional.isPresent());
        assertTrue(consoleOptional.isEmpty());
    }

    @Test
    void deveRetornarOptionalVazioQuandoBuscarPorSlugInexistente() {
        var slugInexistente = "";
        var consoleOptional = repository.findBySlugIgnoreCaseAndAtivoTrue(slugInexistente);

        assertFalse(consoleOptional.isPresent());
        assertTrue(consoleOptional.isEmpty());
    }

}
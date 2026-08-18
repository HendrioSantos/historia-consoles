package historia_consoles.backendConsoles.core.service;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleAtualizar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleCriar;
import historia_consoles.backend_Consoles.core.exclusao.console.ConsoleExclusao;
import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import historia_consoles.backend_Consoles.validadores.ValidadorImagemUrl;
import historia_consoles.backend_Consoles.core.service.ConsoleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes de console service")
class ConsoleServiceTest {

    @InjectMocks
    private ConsoleService service;
    @Mock
    private ConsoleRepository repository;
    @Mock
    private GeracaoRepository geracaoRepository;
    @Mock
    private ValidadorImagemUrl url;
    @Mock
    private FiltradorSlug slug;
    @Mock
    private ConsoleExclusao metodoExclusao;

    @Test
    @DisplayName("Deve salvar console com sucesso")
    void deveSalvarConsoleComSucesso() {
        var jogosIdsMock = List.of(1L, 2L);

        var dto = new ConsoleCriar("console", "fabricante", "publi", "https://imagem.com",
                "100 milhões", 1L, Hardware.builder().cpu("cpu").build(), new Periodo(LocalDate.now(), LocalDate.now()),
                jogosIdsMock);

        var geracao = Geracao.builder()
                .id(1L)
                .nome("Primeira")
                .numeroGeracao(1)
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .ativo(true)
                .atual(false)
                .slug("primeira")
                .fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();

        var console = Console.builder()
                .id(1L)
                .nome("console")
                .fabricante("fabricante")
                .publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracao)
                .imagemUrl("https://imagem.com")
                .unidadesVendidas("100 milhões")
                .ativo(true)
                .slug("console")
                .build();

        when(geracaoRepository.findById(1L)).thenReturn(Optional.of(geracao));
        when(url.validarImagemUrl(any(String.class))).thenReturn("https://imagem.com");
        when(repository.save(any(Console.class))).thenReturn(console);

        var resultado = service.criar(dto);

        assertNotNull(resultado);
        assertEquals("console", resultado.getNome());
    }


    @Test
    @DisplayName("Deve encontrar o console ativo pelo id")
    void deveBuscarConsolePorIdComSucesso(){
        var geracao = Geracao.builder().id(1L).nome("Primeira").numeroGeracao(1)
                .periodo(Periodo.builder()
                        .inicio(LocalDate.now())
                        .fim(LocalDate.now())
                        .build())
                .ativo(true).atual(false).slug(FiltradorSlug.gerarSlug("Primeira")).fatoHistorico("Fatidico dia")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE).geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();

        var geracaoSalva = geracaoRepository.save(geracao);

        var console = Console.builder().id(1L).nome("console").fabricante("fabricante").publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build()).periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .geracao(geracao).imagemUrl("http").ativo(true).slug(FiltradorSlug.gerarSlug("console"))
                .build();

        var consoleSalvo = repository.save(console);

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(console));
        var resultado = service.encontrarConsole(1L);

        assertNotNull(resultado);
        assertEquals("console", resultado.getNome());
    }

    @Test
    @DisplayName("Deve retornar paginas de console com sucesso")
    void deveRetornarPaginaDeConsolesAtivos(){
        var geracao = Geracao.builder().id(1L).nome("Primeira").numeroGeracao(1)
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build())
                .ativo(true).atual(false).slug("primeira").fatoHistorico("Fatidico dia").geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();

        var console = Console.builder().id(1L).nome("console").fabricante("fabricante").publicadora("publi")
                .hardware(Hardware.builder().cpu("cpu").build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build()).geracao(geracao).imagemUrl("http").ativo(true)
                .slug("console").jogos(List.of()).build();

        var consoleLista = List.of(console);
        Page<Console> paginaConsole = new PageImpl<>(consoleLista);
        Pageable pagina = PageRequest.of(0, 10);

        when(repository.findAllByAtivoTrue(pagina)).thenReturn(paginaConsole);

        var resultado = service.listarPaginas(pagina);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("console", resultado.getContent().get(0).nome());
    }

    @Test
    @DisplayName("Deve encontrar console por slug com sucesso")
    void deveBuscarConsolePorSlugComSucesso() {
        var console = Console.builder().nome("PlayStation").slug("playstation").ativo(true).build();

        when(repository.findBySlugIgnoreCaseAndAtivoTrue("playstation")).thenReturn(Optional.of(console));

        var resultado = service.encontrarConsolePorSlug("playstation");

        assertNotNull(resultado);
        assertEquals("PlayStation", resultado.getNome());
    }

    @Test
    @DisplayName("Deve atualizar console com novo nome e nova imagem com sucesso")
    void deveAtualizarConsoleComSucesso() {
        var listaIdJogos = List.of(1L, 2L);

        var dto = new ConsoleAtualizar("Novo", "fab", "pub", "50 milhões", "http://imagem.com",
                Hardware.builder().cpu("cpu").build(),
                Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build(), true, true,
                listaIdJogos, 1L);

        var consoleAntigo = Console.builder().id(1L).nome("Nome Velho").slug("nome-velho").imagemUrl("http://velha.com").build();

        when(repository.findById(1L)).thenReturn(Optional.of(consoleAntigo));
        when(url.validarImagemUrl(any(String.class))).thenReturn("http://nova-url.com");

        when(repository.save(any(Console.class))).thenReturn(consoleAntigo);

        var resultado = service.atualizar(1L, dto);

        assertNotNull(resultado);
        verify(repository).save(consoleAntigo);
    }

    @Test
    @DisplayName("Deve processar a exclusao usando a estrategia correspondente com sucesso")
    void deveProcessarExclusaoComSucesso() {
        List<ConsoleExclusao> listaDeEstrategias = List.of(metodoExclusao);

        ReflectionTestUtils.setField(
                service, "metodoExclusao", listaDeEstrategias
        );

        when(metodoExclusao.exclusaoLogica(true)).thenReturn(true);

        service.processarExclusao(1L, true);

        verify(metodoExclusao).consoleExcluir(1L, repository);
    }

    @Test
    @DisplayName("Deve estourar InvalidoException quando a geração do console não for encontrada")
    void deveEstourarInvalidoExceptionQuandoGeracaoNaoExistir(){
        var jogosIdsMock = List.of(1L, 2L);

        var dto = new ConsoleCriar("console", "fabricante", "publi", "https://imagem.com",
                "100 milhões", 999L, Hardware.builder().cpu("cpu").build(), new Periodo(LocalDate.now(), LocalDate.now()),
                jogosIdsMock);
        when(geracaoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(InvalidoException.class, () -> service.criar(dto));
        verify(repository, never()).save(any(Console.class));
    }

    @Test
    @DisplayName("Deve estourar InvalidoException quando buscar um console por ID inexistente ou inativo")
    void deveEstourarInvalidoExceptionQuandoConsoleNaoExistir() {
        var id = 999L;
        when(repository.findByIdAndAtivoTrue(id)).thenReturn(Optional.empty());

        var e = assertThrows(InvalidoException.class, () -> service.encontrarConsole(id));
        assertEquals("Não encontrei o console", e.getMessage());
    }

    @Test
    @DisplayName("Deve retornar pagina vazia quando nao houver consoles ativos")
    void deveRetornarPaginaVaziaQuandoNaoHouverConsolesAtivos(){
        Page<Console> paginaVazia = Page.empty();
        Pageable paginacao = PageRequest.of(0, 10);

        when(repository.findAllByAtivoTrue(paginacao)).thenReturn(paginaVazia);
        var resultado = service.listarPaginas(paginacao);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        assertEquals(0, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Deve estourar InvalidoException quando o slug do console nao existir")
    void deveEstourarInvalidoExceptionQuandoSlugNaoExistir() {
        when(repository.findBySlugIgnoreCaseAndAtivoTrue("invalido")).thenReturn(Optional.empty());

        var excecao = assertThrows(InvalidoException.class, () -> service.encontrarConsolePorSlug("invalido"));

        assertEquals("Não foi possível encontrar o console pelo slug", excecao.getMessage());
    }
    @Test
    @DisplayName("Deve estourar InvalidoException ao tentar atualizar console inexistente")
    void deveEstourarInvalidoExceptionAoAtualizarConsoleInexistente() {
        var jogosIdsMock = List.of(1L, 2L);

        var dados = new ConsoleAtualizar("Novo", "fab", "pub", "50 milhões", "https://imagem.com",
                Hardware.builder().cpu("cpu").build(),
                Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build(), true, true,
                jogosIdsMock, 1L);

        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(InvalidoException.class, () -> service.atualizar(999L, dados));
        verify(repository, never()).save(any(Console.class));
    }

    @Test
    @DisplayName("Deve estourar InvalidoException se nenhuma estrategia de exclusao for suportada")
    void deveEstourarInvalidoExceptionQuandoEstrategiaNaoSuportada() {
        var listaExclusao = List.of(metodoExclusao);

        ReflectionTestUtils.setField(
                service, "metodoExclusao", listaExclusao
        );

        when(metodoExclusao.exclusaoLogica(true)).thenReturn(false);
        var excecao = assertThrows(InvalidoException.class, () -> service.processarExclusao(1L, true));

        assertEquals("Metodo de exclusão não suportado", excecao.getMessage());
    }
}
package historia_consoles.backendConsoles.core.service;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoAtualizar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoCriar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoDetalhar;
import historia_consoles.backend_Consoles.core.exclusao.jogo.JogoMetodoExclusao;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoMidiaOriginal;
import historia_consoles.backend_Consoles.core.models.enums.JogoStatus;
import historia_consoles.backend_Consoles.core.models.enums.JogoModo;
import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;
import historia_consoles.backend_Consoles.core.repository.JogoRepository;
import historia_consoles.backend_Consoles.validadores.ValidadorImagemUrl;
import historia_consoles.backend_Consoles.core.service.JogoService;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JogoServiceTest {

    @InjectMocks
    private JogoService service;
    @Mock
    private JogoRepository repository;
    @Mock
    private ConsoleRepository consoleRepository;
    @Mock
    private ValidadorImagemUrl url;
    @Mock
    private JogoMetodoExclusao jogoExclusaoMock;

    @Test
    @DisplayName("Deve criar um novo jogo com sucesso")
    void deveCriarJogoComSucesso() {
        var dto = new JogoCriar("Chrono Trigger", "Square", "Square", "4MB", JogoModo.SOLO,
                "http://imagem.com", "https://video.com", "Hironobu Sakaguchi", 1L, JogoGenero.RPG,
                JogoStatus.LANCADO, JogoMidiaOriginal.CARTUCHO, 96);

        var consoleMock = Console.builder().id(1L).ativo(true).nome("SNES").build();
        var jogoEsperado = Jogo.builder().id(10L).nome("Chrono Trigger").console(consoleMock).build();

        when(consoleRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(consoleMock));
        when(url.validarImagemUrl(any(String.class))).thenReturn("http://imagem.com");
        when(repository.save(any(Jogo.class))).thenReturn(jogoEsperado);

        var resultado = service.criar(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Chrono Trigger", resultado.getNome());
    }


    @Test
    @DisplayName("Deve encontrar jogo por slug com sucesso")
    void deveBuscarJogoPorSlugComSucesso() {
        var jogo = Jogo.builder().nome("nome").slug("nome").ativo(true).build();

        when(repository.findBySlugAndAtivoTrue("nome")).thenReturn(Optional.of(jogo));

        JogoDetalhar resultado = service.encontrarJogoPorSlug("nome");

        assertNotNull(resultado);
        assertEquals("nome", resultado.nome());
    }

    @Test
    @DisplayName("Deve listar jogos por console com sucesso")
    void deveListarJogosPorConsoleComSucesso() {
        var consoleValido = Console.builder()
                .id(1L)
                .nome("Super Nintendo")
                .build();

        var jogo = Jogo.builder()
                .nome("Chrono Trigger")
                .console(consoleValido)
                .build();

        Pageable paginacao = PageRequest.of(0, 10);
        Page<Jogo> paginaMock = new PageImpl<>(List.of(jogo));

        when(repository.findAllByConsoleIdAndAtivoTrue(1L, paginacao)).thenReturn(paginaMock);

        var resultado = service.listarPorConsole(1L, paginacao);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        var jogoListagem = resultado.getContent().get(0);
        assertEquals("Chrono Trigger", jogoListagem.nome());
    }


    @Test
    @DisplayName("Deve encontrar jogo por id com sucesso")
    void deveBuscarJogoPorIdComSucesso() {
        var jogo = Jogo.builder().id(1L).nome("Zelda").ativo(true).build();

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(jogo));

        var resultado = service.encontrarJogo(1L);

        assertNotNull(resultado);
        assertEquals("Zelda", resultado.nome());
    }

    @Test
    @DisplayName("Deve retornar pagina de todos os jogos ativos com sucesso")
    void deveListarTodosOsJogosAtivosComSucesso() {
        var consoleValido = Console.builder().id(1L).nome("Super Nintendo").build();
        var jogo = Jogo.builder().nome("Chrono Trigger").console(consoleValido).build();

        Pageable paginacao = PageRequest.of(0, 10);
        Page<Jogo> paginaMock = new PageImpl<>(List.of(jogo));

        when(repository.findAllByAtivoTrue(paginacao)).thenReturn(paginaMock);

        var resultado = service.listarPaginas(paginacao);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Chrono Trigger", resultado.getContent().get(0).nome());
    }

    @Test
    @DisplayName("Deve atualizar jogo existente com sucesso")
    void deveAtualizarJogoComSucesso() {
        var dadosAtualizar = new JogoAtualizar("nomeNovo", "dev", "publi", "4MB",
                "http://imagem.com", "https://video.com", "Diretor", JogoModo.SOLO, JogoGenero.RPG,
                JogoStatus.LANCADO, JogoMidiaOriginal.CARTUCHO, 95, true, false, 2L);

        var jogoAntigo = Jogo.builder().id(1L).nome("Nome Antigo").build();

        when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(jogoAntigo));
        when(url.validarImagemUrl("http://imagem.com")).thenReturn("http://imagem.com");
        when(repository.save(any(Jogo.class))).thenReturn(jogoAntigo);

        var resultado = service.atualizar(1L, dadosAtualizar);

        assertNotNull(resultado);
        verify(repository).save(jogoAntigo);
    }

    @Test
    @DisplayName("Deve processar exclusao de jogo usando a estrategia correspondente com sucesso")
    void deveProcessarExclusaoDoJogoComSucesso() {
        var listaDeEstrategias = List.of(jogoExclusaoMock);

        ReflectionTestUtils.setField(
                service, "metodoExclusao", listaDeEstrategias
        );

        when(jogoExclusaoMock.exclusaoLogica(true)).thenReturn(true);

        service.processarExclusao(1L, true);

        verify(jogoExclusaoMock).JogoExcluir(1L, repository);
    }

    @Test
    @DisplayName("Deve estourar InvalidoException quando buscar jogo por slug inexistente")
    void deveEstourarInvalidoExceptionAoBuscarSlugInexistente() {
        when(repository.findBySlugAndAtivoTrue("slug-fantasma")).thenReturn(Optional.empty());

        var excecao = assertThrows(InvalidoException.class, () -> service.encontrarJogoPorSlug("slug-fantasma"));

        assertEquals("Não foi possível encontrar o jogo pelo slug", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve estourar InvalidoException quando buscar jogo por ID inexistente")
    void deveEstourarInvalidoExceptionAoBuscarIdInexistente() {
        when(repository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        var excecao = assertThrows(InvalidoException.class, () -> service.encontrarJogo(999L));

        assertEquals("Não encontrei o jogo", excecao.getMessage());
    }

    @Test
    @DisplayName("Deve estourar InvalidoException ao criar jogo com console inexistente")
    void deveEstourarInvalidoExceptionAoCriarJogoComConsoleInexistente() {
        var dto = new JogoCriar("Chrono Trigger", "Square", "Square", "4MB", JogoModo.SOLO,
                "http://imagem.com", "https://video.com", "Hironobu Sakaguchi", 999L, JogoGenero.RPG,
                JogoStatus.LANCADO, JogoMidiaOriginal.CARTUCHO, 96);

        when(consoleRepository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        var excecao = assertThrows(InvalidoException.class, () -> service.criar(dto));

        assertEquals("Console não encontrado", excecao.getMessage());

        verify(repository, never()).save(any(Jogo.class));
    }


    @Test
    @DisplayName("Deve estourar InvalidoException ao tentar atualizar um jogo inexistente")
    void deveEstourarInvalidoExceptionAoAtualizarJogoInexistente() {
        var dto = new JogoAtualizar("nomeNovo", "dev", "publi", "4MB",
                "http://imagem.com", "https://video.com", "Diretor", JogoModo.SOLO, JogoGenero.RPG,
                JogoStatus.LANCADO, JogoMidiaOriginal.CARTUCHO, 95, true, false, 2L);
        when(repository.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

        var excecao = assertThrows(InvalidoException.class, () -> service.atualizar(999L, dto));

        assertEquals("Jogo não encontrado", excecao.getMessage());
        verify(repository, never()).save(any(Jogo.class));
    }

    @Test
    @DisplayName("Deve estourar InvalidoException se nenhuma estrategia de exclusao de jogo for suportada")
    void deveEstourarInvalidoExceptionQuandoEstrategiaDeExclusaoNaoSuportada() {
        var listaDeEstrategias = List.of(jogoExclusaoMock);
        ReflectionTestUtils.setField(
                service, "metodoExclusao", listaDeEstrategias
        );

        when(jogoExclusaoMock.exclusaoLogica(true)).thenReturn(false);

        var excecao = assertThrows(InvalidoException.class, () -> service.processarExclusao(1L, true));

        assertEquals("Metodo de exclusão não suportado", excecao.getMessage());
    }
}
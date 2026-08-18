package historia_consoles.backendConsoles.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoAtualizar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoCriar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoDetalhar;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoListagem;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoMidiaOriginal;
import historia_consoles.backend_Consoles.core.models.enums.JogoStatus;
import historia_consoles.backend_Consoles.core.models.enums.JogoModo;
import historia_consoles.backend_Consoles.core.service.JogoService;
import historia_consoles.backend_Consoles.testconfig.TestConfig;
import historia_consoles.backend_Consoles.core.controller.JogoController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(JogoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = TestConfig.class)
class JogoControllerTest {

    @MockBean
    private JogoService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar status 200 ao listar jogos paginados")
    void deveListarJogosComSucesso() throws Exception {
        var geracao = Geracao.builder().id(1L).nome("Quarta Geração").build();
        var console = Console.builder().id(1L).nome("Super Nintendo").geracao(geracao).jogos(List.of()).build();
        var jogo = Jogo.builder().id(1L).nome("Chrono Trigger").console(console).build();
        var dto = new JogoListagem(jogo);
        Page<JogoListagem> paginaMock = new PageImpl<>(List.of(dto));

        when(service.listarPaginas(any(Pageable.class))).thenReturn(paginaMock);

        mockMvc.perform(MockMvcRequestBuilders.get("/jogo")
                        .param("consoleId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao listar jogos por console")
    void deveListarJogosPorConsoleComSucesso() throws Exception {
        var geracao = Geracao.builder().id(1L).nome("Quarta Geração").build();
        var console = Console.builder().id(1L).nome("Super Nintendo").geracao(geracao).jogos(List.of()).build();
        var jogo = Jogo.builder().id(1L).nome("Super Mario World").console(console).build();
        var dto = new JogoListagem(jogo);
        Page<JogoListagem> pagina = new PageImpl<>(List.of(dto));

        when(service.listarPorConsole(eq(1L), any(Pageable.class))).thenReturn(pagina);

        mockMvc.perform(MockMvcRequestBuilders.get("/jogo")
                        .param("consoleId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar jogo por ID")
    void deveBuscarJogoPorIdComSucesso() throws Exception {
        var geracao = Geracao.builder().id(1L).nome("Quarta Geração").build();
        var console = Console.builder().id(1L).nome("Super Nintendo").geracao(geracao).jogos(List.of()).build();
        var jogo = Jogo.builder().id(1L).nome("Chrono Trigger").console(console).build();
        var dto = new JogoDetalhar(jogo);

        when(service.encontrarJogo(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/jogo/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar jogo por Slug")
    void deveBuscarJogoPorSlugComSucesso() throws Exception {
        var geracao = Geracao.builder().id(1L).nome("Quarta Geração").build();
        var console = Console.builder().id(1L).nome("Super Nintendo").geracao(geracao).jogos(List.of()).build();
        var jogo = Jogo.builder().id(1L).nome("Chrono Trigger").slug("chrono-trigger").console(console).build();
        var dto = new JogoDetalhar(jogo);

        when(service.encontrarJogoPorSlug("chrono-trigger")).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/jogo/slug/{slug}", "chrono-trigger")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 201 ao criar um novo jogo valido")
    void deveCriarJogoComSucesso() throws Exception {
        var dtoCriar = new JogoCriar("Chrono Trigger", "Square", "Square", "4MB",
                JogoModo.SOLO, "http://imagem.com", "https://video.com", "Sakaguchi", 1L,
                JogoGenero.RPG, JogoStatus.LANCADO, JogoMidiaOriginal.CARTUCHO, 96);
        var geracao = Geracao.builder().id(1L).nome("Quarta Geração").build();
        var console = Console.builder().id(1L).nome("Super Nintendo").geracao(geracao).jogos(List.of()).build();
        var jogoSalvo = Jogo.builder().id(10L).nome("Chrono Trigger").console(console).build();

        when(service.criar(any(JogoCriar.class))).thenReturn(jogoSalvo);

        var jsonInput = objectMapper.writeValueAsString(dtoCriar);

        mockMvc.perform(MockMvcRequestBuilders.post("/jogo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao atualizar dados de um jogo")
    void deveAtualizarJogoComSucesso() throws Exception {
        var dtoAtualizar = new JogoAtualizar("Nome Novo", "dev", "publi", "4MB",
                "http://imagem.com", "https://video.com", "Diretor", JogoModo.SOLO, JogoGenero.RPG,
                JogoStatus.LANCADO, JogoMidiaOriginal.CARTUCHO, 95, true, false, 1L);
        var geracao = Geracao.builder().id(1L).nome("Quarta Geração").build();
        var console = Console.builder().id(1L).nome("Super Nintendo").geracao(geracao).jogos(List.of()).build();
        var jogoAtualizado = Jogo.builder().id(1L).nome("Nome Novo").console(console).build();

        when(service.atualizar(eq(1L), any(JogoAtualizar.class))).thenReturn(jogoAtualizado);

        var jsonInput = objectMapper.writeValueAsString(dtoAtualizar);

        mockMvc.perform(MockMvcRequestBuilders.put("/jogo/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 204 ao remover um jogo")
    void deveDeletarJogoComSucesso() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/jogo/{id}", 1L)
                        .param("logico", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service).processarExclusao(1L, true);
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar criar jogo com dados invalidos no DTO")
    void deveRetornar400AoCriarJogoInvalido() throws Exception {
        var dtoInvalido = new JogoCriar("", "", "", "", null,
                "", "", "", null, null, null, null, 0);
        var jsonInput = objectMapper.writeValueAsString(dtoInvalido);

        mockMvc.perform(MockMvcRequestBuilders.post("/jogo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar listar jogos sem o parametro obrigatorio consoleId")
    void deveRetornar400AoListarJogosSemParametroConsoleId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/jogo")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 quando o service nao encontrar o jogo por ID")
    void deveRetornar400AoBuscarJogoPorIdInexistente() throws Exception {
        when(service.encontrarJogo(999L)).thenThrow(new InvalidoException("Não encontrei o jogo"));

        mockMvc.perform(MockMvcRequestBuilders.get("/jogo/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 quando o service nao encontrar o jogo por Slug")
    void deveRetornar400AoBuscarJogoPorSlugInexistente() throws Exception {
        when(service.encontrarJogoPorSlug("slug-fantasma")).thenThrow(new InvalidoException("Não foi possível encontrar o jogo pelo slug"));

        mockMvc.perform(MockMvcRequestBuilders.get("/jogo/slug/{slug}", "slug-fantasma")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar atualizar um jogo que nao existe")
    void deveRetornar400AoAtualizarJogoInexistente() throws Exception {
        var dtoAtualizar = new JogoAtualizar("Nome Novo", "dev", "publi", "4MB",
                "http://imagem.com", "https://video.com", "Diretor", JogoModo.SOLO, JogoGenero.RPG,
                JogoStatus.LANCADO, JogoMidiaOriginal.CARTUCHO, 95, true, false, 1L);
        var jsonInput = objectMapper.writeValueAsString(dtoAtualizar);

        when(service.atualizar(eq(999L), any(JogoAtualizar.class))).thenThrow(new InvalidoException("Jogo não encontrado"));

        mockMvc.perform(MockMvcRequestBuilders.put("/jogo/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
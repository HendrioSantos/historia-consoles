package historia_consoles.backendConsoles.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.controller.ConsoleController;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleAtualizar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleCriar;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleListagem;
import historia_consoles.backend_Consoles.core.models.entities.Hardware;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Console;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.service.ConsoleService;
import historia_consoles.backend_Consoles.testconfig.TestConfig;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ConsoleController.class)
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class ConsoleControllerTest {

    @MockBean
    private ConsoleService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Deve retornar status 200 ao listar consoles paginados")
    void deveListarConsolesComSucesso() throws Exception {
        var geracao = Geracao.builder().id(1L).nome("Quarta Geração").build();

        var console = Console.builder().id(1L).nome("PlayStation 5").fabricante("Sony").publicadora("Sony")
                .unidadesVendidas("60 milhões").slug("playstation-5").imagemUrl("http://imagem.com").hardware(Hardware.builder().build())
                .periodo(Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build()).ativo(true).descontinuado(false)
                .retrocompatibilidade(true).geracao(geracao).jogos(List.of()).build();

        var dto = new ConsoleListagem(console);
        Page<ConsoleListagem> pagina = new PageImpl<>(List.of(dto));

        when(service.listarPaginas(any(Pageable.class))).thenReturn(pagina);

        mockMvc.perform(MockMvcRequestBuilders.get("/console")
                        .param("geracaoId", "1")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar console por ID")
    void deveBuscarConsolePorIdComSucesso() throws Exception {
        var console = Console.builder().id(1L).nome("Nintendo Switch").build();

        when(service.encontrarConsole(1L)).thenReturn(console);

        mockMvc.perform(MockMvcRequestBuilders.get("/console/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar console por Slug")
    void deveBuscarConsolePorSlugComSucesso() throws Exception {
        var console = Console.builder().id(1L).nome("Super Nintendo").slug("snes").build();

        when(service.encontrarConsolePorSlug("snes")).thenReturn(console);

        mockMvc.perform(MockMvcRequestBuilders.get("/console/slug/{slug}", "snes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 201 ao criar um novo console valido")
    void deveCriarConsoleComSucesso() throws Exception {
        var dtoCriar = new ConsoleCriar("Mega Drive", "Sega", "Sega", "https://imagem.com",
                "40 milhões", 1L, Hardware.builder().build(), new Periodo(LocalDate.now(), LocalDate.now()), List.of());
        var consoleSalvo = Console.builder().id(5L).nome("Mega Drive").build();

        when(service.criar(any(ConsoleCriar.class))).thenReturn(consoleSalvo);

        var jsonInput = objectMapper.writeValueAsString(dtoCriar);

        mockMvc.perform(MockMvcRequestBuilders.post("/console")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    @DisplayName("Deve retornar status 200 ao atualizar dados de um console")
    void deveAtualizarConsoleComSucesso() throws Exception {
        var dtoAtualizar = new ConsoleAtualizar("Nome Modificado", "fab", "pub", "50M",
                "http://imagem.com", Hardware.builder().build(), new Periodo(LocalDate.now(), LocalDate.now()), true,
                true, List.of(1L), 1L);
        var consoleAtualizado = Console.builder().id(1L).nome("Nome Modificado").build();

        when(service.atualizar(eq(1L), any(ConsoleAtualizar.class))).thenReturn(consoleAtualizado);

        var jsonInput = objectMapper.writeValueAsString(dtoAtualizar);

        mockMvc.perform(MockMvcRequestBuilders.put("/console/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 204 ao remover um console")
    void deveDeletarConsoleComSucesso() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/console/{id}", 1L)
                        .param("logico", "true")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service).processarExclusao(1L, true);
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar criar console com dados invalidos")
    void deveRetornar400AoCriarConsoleInvalido() throws Exception {
        var dtoInvalido = new ConsoleCriar("", "", "", "", "", null,
                null, null, List.of());
        var jsonInput = objectMapper.writeValueAsString(dtoInvalido);

        mockMvc.perform(MockMvcRequestBuilders.post("/console")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar listar consoles sem o parametro geracaoId")
    void deveRetornar400AoListarConsolesSemParametroObrigatorio() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/console")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 quando o service nao encontrar o console por ID")
    void deveRetornar400AoBuscarConsolePorIdInexistente() throws Exception {
        when(service.encontrarConsole(999L)).thenThrow(new InvalidoException("Não encontrei o console"));

        mockMvc.perform(MockMvcRequestBuilders.get("/console/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 400 quando o service nao encontrar o console por Slug")
    void deveRetornar400AoBuscarConsolePorSlugInexistente() throws Exception {
        when(service.encontrarConsolePorSlug("slug-fantasma")).thenThrow(new InvalidoException("Não foi possível encontrar o console pelo slug"));

        mockMvc.perform(MockMvcRequestBuilders.get("/console/slug/{slug}", "slug-fantasma")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}
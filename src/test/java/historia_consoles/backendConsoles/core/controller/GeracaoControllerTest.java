package historia_consoles.backendConsoles.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoAtualizar;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoCriar;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoListagem;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import historia_consoles.backend_Consoles.core.service.GeracaoService;
import historia_consoles.backend_Consoles.testconfig.TestConfig;
import historia_consoles.backend_Consoles.core.controller.GeracaoController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(GeracaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = TestConfig.class)
class GeracaoControllerTest {

    @MockBean
    private GeracaoService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("Deve retornar status 200 ao listar geracoes paginadas")
    void deveListarGeracoesComSucesso() throws Exception {
        var geracao = Geracao.builder()
                .id(1L)
                .nome("Primeira")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .geracaoEmpresaDominante(GeracaoEmpresaDominante.ATARI_DOMINANTE)
                .build();
        var dto = new GeracaoListagem(geracao);
        var paginaMock = new PageImpl<>(List.of(dto));

        when(service.listarPaginas(any(Pageable.class))).thenReturn(paginaMock);

        mockMvc.perform(MockMvcRequestBuilders.get("/geracao")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar geracao por ID")
    void deveBuscarGeracaoPorIdComSucesso() throws Exception {
        var geracao = Geracao.builder().id(4L).nome("Quarta")
                .geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .build();

        when(service.encontrarGeracao(4L)).thenReturn(geracao);

        mockMvc.perform(MockMvcRequestBuilders.get("/geracao/{id}", 4L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao buscar geracao por Slug")
    void deveBuscarGeracaoPorSlugComSucesso() throws Exception {
        var geracao = Geracao.builder().id(1L).nome("Primeira").geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .slug("quarta-geracao").build();

        when(service.encontrarGeracaoPorSlug("quarta-geracao")).thenReturn(geracao);

        mockMvc.perform(MockMvcRequestBuilders.get("/geracao/slug/{slug}", "quarta-geracao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 201 ao criar uma nova geracao valida")
    void deveCriarGeracaoComSucesso() throws Exception {
        var dtoCriar = new GeracaoCriar("geracao","fato", 1, GeracaoCronologia.PIONEIROS_OU_VINTAGE,
                GeracaoEmpresaDominante.ATARI_DOMINANTE, new Periodo(LocalDate.now(), LocalDate.now()));
        var geracaoSalva = Geracao.builder().id(1L).nome("Primeira").geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE).build();

        when(service.criarGeracao(any(GeracaoCriar.class))).thenReturn(geracaoSalva);

        var jsonInput = objectMapper.writeValueAsString(dtoCriar);

        mockMvc.perform(MockMvcRequestBuilders.post("/geracao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));
    }

    @Test
    @DisplayName("Deve retornar status 200 ao atualizar dados de uma geracao")
    void deveAtualizarGeracaoComSucesso() throws Exception {
        var dtoAtualizar = new GeracaoAtualizar("nome", 1,"fato", GeracaoCronologia.PIONEIROS_OU_VINTAGE,
                GeracaoEmpresaDominante.ATARI_DOMINANTE,new Periodo(LocalDate.now(), LocalDate.now()), false, true);
        var geracaoAtualizada = Geracao.builder().id(1L).geracaoCronologia(GeracaoCronologia.PIONEIROS_OU_VINTAGE)
                .nome("Nome Modificado").build();

        when(service.atualizarGeracao(eq(1L), any(GeracaoAtualizar.class))).thenReturn(geracaoAtualizada);

        var jsonInput = objectMapper.writeValueAsString(dtoAtualizar);

        mockMvc.perform(MockMvcRequestBuilders.put("/geracao/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 204 ao remover uma geracao")
    void deveDeletarGeracaoLogicoTrueComSucesso() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/geracao/{id}", 1L)
                        .param("logico", "true")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service).deletarGeracao(1L, true);
    }
    @Test
    @DisplayName("Deve retornar status 204 ao remover uma geracao")
    void deveDeletarGeracaoLogicoFalseComSucesso() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/geracao/{id}", 1L)
                        .param("logico", "false")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(service).deletarGeracao(1L, false);
    }

    @Test
    @DisplayName("Deve retornar status 400 ao tentar criar geracao com dados invalidos no DTO")
    void deveRetornar400AoCriarGeracaoInvalida() throws Exception {
        var dtoInvalido = new GeracaoCriar("","",0,null,null,null);

        String jsonInput = objectMapper.writeValueAsString(dtoInvalido);

        mockMvc.perform(MockMvcRequestBuilders.post("/geracao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status HTTP de erro quando buscar ID inexistente")
    void deveRetornarErroAoBuscarIdInexistente() throws Exception {
        when(service.encontrarGeracao(999L)).thenThrow(new InvalidoException("Geração não encontrada"));

        mockMvc.perform(MockMvcRequestBuilders.get("/geracao/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status HTTP de erro quando buscar slug inexistente")
    void deveRetornarErroAoBuscarSlugInexistente() throws Exception {
        when(service.encontrarGeracaoPorSlug("slug-que-nao-existe"))
                .thenThrow(new InvalidoException("Geração não encontrada"));

        mockMvc.perform(MockMvcRequestBuilders.get("/geracao/slug/{slug}", "slug-que-nao-existe")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status HTTP de erro ao tentar atualizar geracao inexistente")
    void deveRetornarErroAoAtualizarGeracaoInexistente() throws Exception {
        var dto = new GeracaoAtualizar("Nome Novo", 4,"Fato Historico Novo",
                GeracaoCronologia.PIONEIROS_OU_VINTAGE,GeracaoEmpresaDominante.ATARI_DOMINANTE, null,true, true);

        var jsonInput = objectMapper.writeValueAsString(dto);

        when(service.atualizarGeracao(eq(999L), any(GeracaoAtualizar.class))).thenThrow(new InvalidoException("Geração não encontrada"));

        mockMvc.perform(MockMvcRequestBuilders.put("/geracao/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

}

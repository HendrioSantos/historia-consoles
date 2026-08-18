package historia_consoles.backendConsoles.infra.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import historia_consoles.backend_Consoles.aplicacao.BackendConsolesApplication;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoCriar;
import historia_consoles.backend_Consoles.core.models.entities.Periodo;
import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import historia_consoles.backend_Consoles.core.service.GeracaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@ContextConfiguration(classes = BackendConsolesApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = NONE)
@DisplayName("Teste de Autorizacao de Rotas (Security)")
class SecurityAutorizacaoTest {

    @MockBean
    private GeracaoService geracaoService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "LEITOR")
    @DisplayName("Deve permitir GET para usuario com cargo de LEITOR")
    void devePermitirGetParaLeitor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/geracao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(roles = "LEITOR")
    @DisplayName("Deve retornar status 403 Forbidden ao tentar fazer POST com cargo de LEITOR")
    void deveBarrarPostParaLeitor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/geracao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CARGO_GERACAO")
    @DisplayName("Deve permitir POST para usuario com cargo de CARGO_GERACAO")
    void devePermitirPostParaCargoGeracao() throws Exception {
        var dtoValido = new GeracaoCriar(
                "Quarta Geração",
                "Fato Histórico Relevante",
                4,
                GeracaoCronologia.PIONEIROS_OU_VINTAGE,
                GeracaoEmpresaDominante.ATARI_DOMINANTE,
                Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build()
        );

        var jsonInput = objectMapper.writeValueAsString(dtoValido);

        var geracaoSalva = Geracao.builder().id(1L).nome("Quarta Geração").build();
        when(geracaoService.criarGeracao(any(GeracaoCriar.class))).thenReturn(geracaoSalva);

        mockMvc.perform(MockMvcRequestBuilders.post("/geracao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(roles = "CARGO_CONSOLE")
    @DisplayName("Deve retornar status 403 Forbidden ao tentar fazer POST em geracao usando cargo de outro escopo")
    void deveBarrarPostDeGeracaoParaCargoConsole() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/geracao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Deve permitir POST para usuario com cargo de ADMIN")
    void devePermitirPostParaAdmin() throws Exception {
        var dtoValido = new GeracaoCriar("Quarta Geração", "Fato Histórico Relevante", 4,
                GeracaoCronologia.PIONEIROS_OU_VINTAGE, GeracaoEmpresaDominante.ATARI_DOMINANTE,
                Periodo.builder().inicio(LocalDate.now()).fim(LocalDate.now()).build());
        var geracaoSalva = Geracao.builder().id(1L).nome("Quarta Geração").build();
        var jsonInput = objectMapper.writeValueAsString(dtoValido);

        when(geracaoService.criarGeracao(any(GeracaoCriar.class))).thenReturn(geracaoSalva);

        mockMvc.perform(MockMvcRequestBuilders.post("/geracao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @DisplayName("Deve disparar 401 erro de seguranca ao tentar acessar com um token JWT invalido")
    void deveRetornar401AoPassarTokenInvalido() {
        assertThrows(Exception.class, () -> mockMvc.perform(MockMvcRequestBuilders.get("/geracao")
                .header("Authorization", "Bearer token_completamente_errado_e_malformado")
                .contentType(MediaType.APPLICATION_JSON)));
    }

    @Test
    @DisplayName("Deve retornar status 403 Forbidden ao tentar acessar qualquer rota sem estar autenticado")
    void deveBarrarUsuarioAnonimo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/geracao")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}

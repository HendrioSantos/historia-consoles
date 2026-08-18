package historia_consoles.backendConsoles.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import historia_consoles.backend_Consoles.infra.security.TokenService;
import historia_consoles.backend_Consoles.testconfig.TestConfig;
import historia_consoles.backend_Consoles.usuario.*;
import historia_consoles.backend_Consoles.usuario.dto.DadosAutenticacao;
import historia_consoles.backend_Consoles.usuario.dto.DadosCadastroUsuario;
import historia_consoles.backend_Consoles.usuario.dto.DadosLoginResposta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(AutenticacaoController.class)
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class AutenticacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AutenticacaoService autenticacaoService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private AuthenticationManager manager;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Deve retornar status 200 ao efetuar login com credenciais corretas")
    void deveEfetuarLoginComSucesso() throws Exception {
        var dtoLogin = new DadosAutenticacao("admin", "senha123");
        var respostaMock = new DadosLoginResposta("admin", "token_valido", "Login realizado com sucesso", Role.ADMIN);

        when(autenticacaoService.autenticar(any(DadosAutenticacao.class))).thenReturn(respostaMock);

        var jsonInput = objectMapper.writeValueAsString(dtoLogin);

        mockMvc.perform(MockMvcRequestBuilders.post("/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Deve retornar status 400 quando as credenciais de login estiverem incorretas")
    void deveRetornar400AoTentarLoginComSenhaIncorreta() throws Exception {
        var dtoLogin = new DadosAutenticacao("admin", "senha_errada");

        when(autenticacaoService.autenticar(any(DadosAutenticacao.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        var jsonInput = objectMapper.writeValueAsString(dtoLogin);

        mockMvc.perform(MockMvcRequestBuilders.post("/autenticacao/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao registrar um novo usuario valido")
    void deveRegistrarUsuarioComSucesso() throws Exception {
        var dtoCadastro = new DadosCadastroUsuario("novo_usuario", "senha123", Role.LEITOR, true);
        var usuarioSalvo = Usuario.builder().id(1L).login("novo_usuario").role(Role.LEITOR).ativo(true).build();

        when(usuarioService.registrarUsuario(any(DadosCadastroUsuario.class))).thenReturn(usuarioSalvo);

        var jsonInput = objectMapper.writeValueAsString(dtoCadastro);

        mockMvc.perform(MockMvcRequestBuilders.post("/autenticacao/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInput))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

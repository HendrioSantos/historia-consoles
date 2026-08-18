package historia_consoles.backendConsoles.infra.security;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.usuario.Role;
import historia_consoles.backend_Consoles.usuario.Usuario;
import historia_consoles.backend_Consoles.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teste do TokenService JWT")
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        String secretChaveTeste = "string_secreta_de_teste_com_mais_de_32_caracteres";
        ReflectionTestUtils.setField(tokenService, "secret", secretChaveTeste);
    }

    @Test
    @DisplayName("Deve gerar um token JWT valido com issuer e subject corretos")
    void deveGerarTokenComSucesso() {
        var usuario = Usuario.builder().login("admin_developer").role(Role.ADMIN).build();

        var tokenGerado = tokenService.gerarToken(usuario);

        assertNotNull(tokenGerado);
        assertFalse(tokenGerado.isBlank());

        var loginExtraido = tokenService.validarToken(tokenGerado);
        assertEquals("admin_developer", loginExtraido);
    }

    @Test
    @DisplayName("Deve estourar InvalidoException ao tentar validar um token corrompido ou malformado")
    void deveEstourarExcecaoAoValidarTokenInvalido() {
        var tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidTokenText";

        var excecao = assertThrows(InvalidoException.class, () -> tokenService.validarToken(tokenInvalido));

        assertTrue(excecao.getMessage().contains("Erro ao validar o token"));
    }

    @Test
    @DisplayName("Deve estourar IllegalArgumentException ao tentar gerar token se a assinatura secret estiver nula")
    void deveEstourarIllegalArgumentExceptionSeSecretEstiverNulo() {
        ReflectionTestUtils.setField(tokenService, "secret", null);
        var usuario = Usuario.builder().login("user").build();

        assertThrows(IllegalArgumentException.class, () -> tokenService.gerarToken(usuario));
    }
}

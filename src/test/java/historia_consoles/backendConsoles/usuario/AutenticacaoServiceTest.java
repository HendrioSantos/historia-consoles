package historia_consoles.backendConsoles.usuario;

import historia_consoles.backend_Consoles.infra.security.TokenService;
import historia_consoles.backend_Consoles.usuario.dto.DadosAutenticacao;
import historia_consoles.backend_Consoles.usuario.AutenticacaoService;
import historia_consoles.backend_Consoles.usuario.Role;
import historia_consoles.backend_Consoles.usuario.Usuario;
import historia_consoles.backend_Consoles.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teste do AutenticacaoService")
class AutenticacaoServiceTest {

    @InjectMocks
    private AutenticacaoService service;

    @Mock
    private AuthenticationManager manager;

    @Mock
    private Authentication authentication;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("Deve carregar UserDetails por username com sucesso")
    void deveCarregarUserByUsernameComSucesso() {
        var usuario = Usuario.builder().login("user_teste").build();
        when(repository.findByLogin("user_teste")).thenReturn(usuario);

        var resultado = service.loadUserByUsername("user_teste");

        assertNotNull(resultado);
        assertEquals("user_teste", resultado.getUsername());
    }

    @Test
    @DisplayName("Deve autenticar usuario e retornar token JWT com sucesso")
    void deveAutenticarUsuarioComSucesso() {
        var dto = new DadosAutenticacao("login_valido", "senha123");
        var usuario = Usuario.builder().login("login_valido").senha("hash").role(Role.ADMIN).build();

        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(tokenService.gerarToken(usuario)).thenReturn("token_jwt_valido");

        var resultado = service.autenticar(dto);

        assertNotNull(resultado);
        assertEquals("login_valido", resultado.login());
        assertEquals("token_jwt_valido", resultado.token());
        assertEquals(Role.ADMIN, resultado.role());
        assertEquals("Login realizado com sucesso", resultado.mensagem());
    }

    @Test
    @DisplayName("Deve estourar BadCredentialsException quando as credenciais estiverem erradas")
    void deveEstourarBadCredentialsExceptionAoAutenticarComSenhaInvalida() {
        var dto = new DadosAutenticacao("login_valido", "senha_errada");

        when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> service.autenticar(dto));

        verify(tokenService, never()).gerarToken(any(Usuario.class));
    }
}

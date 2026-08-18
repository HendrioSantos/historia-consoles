package historia_consoles.backendConsoles.usuario;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.usuario.dto.AtualizarUsuario;
import historia_consoles.backend_Consoles.usuario.dto.DadosCadastroUsuario;
import historia_consoles.backend_Consoles.usuario.exclusao.UsuarioExclusao;
import historia_consoles.backend_Consoles.usuario.Role;
import historia_consoles.backend_Consoles.usuario.Usuario;
import historia_consoles.backend_Consoles.usuario.UsuarioRepository;
import historia_consoles.backend_Consoles.usuario.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teste do UsuarioService")
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioExclusao usuarioExclusao;

    @Mock
    private PasswordEncoder encoder;

    @Test
    @DisplayName("Deve registrar um novo usuario com senha criptografada com sucesso")
    void deveRegistrarUsuarioComSucesso() {
        var dto = new DadosCadastroUsuario("usuario", "senha123", Role.LEITOR, true);
        var usuario = Usuario.builder().id(1L).login("usuario").senha("senha").role(Role.LEITOR).ativo(true).build();

        when(encoder.encode("senha123")).thenReturn("senha");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        var resultado = service.registrarUsuario(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("senha", resultado.getPassword());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve atualizar informacoes de um usuario existente com sucesso")
    void deveAtualizarInformacoesComSucesso() {
        var dto = new AtualizarUsuario("admin", "nova_senha", Role.ADMIN, true);
        var usuario = Usuario.builder().id(1L).login("admin").senha("velha_senha").role(Role.LEITOR).ativo(true).build();

        when(usuarioRepository.findByLogin("admin")).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        var resultado = service.atualizarInformacoes(dto);

        assertNotNull(resultado);
        assertEquals("nova_senha", resultado.getPassword());
        assertEquals(Role.ADMIN, resultado.getRole());
    }

    @Test
    @DisplayName("Deve estourar RuntimeException ao tentar atualizar um usuario que nao existe")
    void deveEstourarRuntimeExceptionAoAtualizarUsuarioInexistente() {
        var dto = new AtualizarUsuario("fantasma", "senha", Role.LEITOR, true);

        when(usuarioRepository.findByLogin("fantasma")).thenReturn(null);

        var excecao = assertThrows(InvalidoException.class, () -> service.atualizarInformacoes(dto));

        assertEquals("Usuário não encontrado", excecao.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve processar exclusao de usuario usando a estrategia correspondente com sucesso")
    void deveProcessarExclusaoComSucesso() {
        var exclusao = List.of(usuarioExclusao);
        ReflectionTestUtils.setField(service, "metodoExclusao", exclusao);

        when(usuarioExclusao.exclusaoLogica(true)).thenReturn(true);

        service.processarExclusao("admin", true);

        verify(usuarioExclusao).usuarioExcluir("admin", usuarioRepository);
    }

    @Test
    @DisplayName("Deve estourar InvalidoException se nenhuma estrategia de exclusao for suportada")
    void deveEstourarInvalidoExceptionQuandoEstrategiaDeExclusaoNaoSuportada() {
        var listaDeEstrategias = List.of(usuarioExclusao);
        ReflectionTestUtils.setField(service, "metodoExclusao", listaDeEstrategias);

        when(usuarioExclusao.exclusaoLogica(true)).thenReturn(false);

        var excecao = assertThrows(InvalidoException.class, () -> service.processarExclusao("admin", true));

        assertEquals("Metodo de exclusão não suportado", excecao.getMessage());
    }
}

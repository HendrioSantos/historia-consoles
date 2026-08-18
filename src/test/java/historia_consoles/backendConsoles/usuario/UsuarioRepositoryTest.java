package historia_consoles.backendConsoles.usuario;

import historia_consoles.backend_Consoles.aplicacao.BackendConsolesApplication;
import historia_consoles.backend_Consoles.usuario.Role;
import historia_consoles.backend_Consoles.usuario.Usuario;
import historia_consoles.backend_Consoles.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(classes = BackendConsolesApplication.class)
@DisplayName("Testando o usuario repository")
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    private Usuario criarUsuarioAtivo() {
        return Usuario.builder()
                .login("admin_test_" + System.currentTimeMillis())
                .senha("$argon2id$v=19$m=16384,t=2,p=1$bW9ja2VkX3NhbHQ$mocked_hash_value")
                .role(Role.ADMIN)
                .ativo(true)
                .build();
    }

    private Usuario criarUsuarioInativo() {
        return Usuario.builder()
                .login("inativo_test_" + System.currentTimeMillis())
                .senha("$argon2id$v=19$m=16384,t=2,p=1$bW9ja2VkX3NhbHQ$mocked_hash_value")
                .role(Role.LEITOR)
                .ativo(false)
                .build();
    }

    @Test
    @DisplayName("Deve encontrar UserDetails por login com sucesso")
    void deveEncontrarUserDetailsPorLogin() {
        var usuario = criarUsuarioAtivo();
        repository.saveAndFlush(usuario);

        var resultado = repository.findByLogin(usuario.getLogin());

        assertNotNull(resultado);
        assertEquals(usuario.getLogin(), resultado.getUsername());
    }

    @Test
    @DisplayName("Deve retornar nulo ao buscar UserDetails por login inexistente")
    void deveRetornarNuloParaLoginInexistente() {
        var resultado = repository.findByLogin("usuario_fantasma_999");
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve encontrar usuario por ID apenas se estiver ativo")
    void deveBuscarUsuarioPorIdEAtivoTrue() {
        var usuario = criarUsuarioAtivo();
        var usuarioSalvo = repository.saveAndFlush(usuario);

        var resultado = repository.findByIdAndAtivoTrue(usuarioSalvo.getId());

        assertTrue(resultado.isPresent());
        assertEquals(usuarioSalvo, resultado.get());
    }

    @Test
    @DisplayName("Nao deve encontrar usuario por ID se ele estiver inativo")
    void naoDeveBuscarUsuarioPorIdSeInativo() {
        var usuario = criarUsuarioInativo();
        var usuarioSalvo = repository.saveAndFlush(usuario);

        var resultado = repository.findByIdAndAtivoTrue(usuarioSalvo.getId());

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve encontrar usuario ativo por login retornando Optional")
    void deveBuscarUsuarioPorLoginEAtivoTrue() {
        var usuario = criarUsuarioAtivo();
        repository.saveAndFlush(usuario);

        var resultado = repository.findByLoginAndAtivoTrue(usuario.getLogin());

        assertTrue(resultado.isPresent());
        assertEquals(usuario.getLogin(), resultado.get().getLogin());
    }

    @Test
    @DisplayName("Nao deve encontrar usuario por login se ele estiver inativo no banco")
    void naoDeveBuscarUsuarioPorLoginSeInativo() {
        var usuario = criarUsuarioInativo();
        repository.saveAndFlush(usuario);

        var resultado = repository.findByLoginAndAtivoTrue(usuario.getLogin());

        assertTrue(resultado.isEmpty());
    }

}
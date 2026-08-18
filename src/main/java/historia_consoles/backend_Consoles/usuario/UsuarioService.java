package historia_consoles.backend_Consoles.usuario;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.usuario.dto.AtualizarUsuario;
import historia_consoles.backend_Consoles.usuario.dto.DadosCadastroUsuario;
import historia_consoles.backend_Consoles.usuario.exclusao.UsuarioExclusao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final List<UsuarioExclusao> metodoExclusao;

    @Transactional(readOnly = true)
    private Usuario encontrarUsuario(String login){
        var usuario = usuarioRepository.findByLogin(login);
        if (usuario == null){
            throw new InvalidoException("Usuário não encontrado");
        }
        return (Usuario) usuario;
    }

    @Transactional
    public Usuario registrarUsuario(DadosCadastroUsuario dados) {
        var usuario = Usuario.builder()
                .login(dados.login())
                .senha(encoder.encode(dados.senha()))
                .role(dados.role())
                .ativo(true)
                .build();
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizarInformacoes(AtualizarUsuario dados){
        var usuario = encontrarUsuario(dados.login());
        usuario.atualizarInformacoes(dados);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void processarExclusao(String login, boolean logico){
        metodoExclusao.stream()
                .filter(m -> m.exclusaoLogica(logico))
                .findFirst()
                .orElseThrow(() -> new InvalidoException("Metodo de exclusão não suportado"))
                .usuarioExcluir(login, usuarioRepository);
    }

}

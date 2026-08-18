package historia_consoles.backend_Consoles.usuario;

import historia_consoles.backend_Consoles.infra.security.TokenService;
import historia_consoles.backend_Consoles.usuario.dto.DadosAutenticacao;
import historia_consoles.backend_Consoles.usuario.dto.DadosLoginResposta;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final AuthenticationManager manager;
    private final TokenService tokenService;

    public AutenticacaoService(UsuarioRepository repository, @Lazy AuthenticationManager manager, TokenService tokenService) {
        this.repository = repository;
        this.manager = manager;
        this.tokenService = tokenService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }

    @Transactional
    public DadosLoginResposta autenticar(DadosAutenticacao dados){
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.login(), dados.senha());
        Authentication authentication = manager.authenticate(authenticationToken);

        var usuario = (Usuario) authentication.getPrincipal();

        var tokenJWT = tokenService.gerarToken(usuario);
        return new DadosLoginResposta(
                usuario.getLogin(),
                tokenJWT,
                "Login realizado com sucesso",
                usuario.getRole()
        );
    }

}

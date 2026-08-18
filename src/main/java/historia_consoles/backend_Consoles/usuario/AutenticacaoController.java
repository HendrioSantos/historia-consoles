package historia_consoles.backend_Consoles.usuario;

import historia_consoles.backend_Consoles.usuario.dto.DadosAutenticacao;
import historia_consoles.backend_Consoles.usuario.dto.DadosCadastroUsuario;
import historia_consoles.backend_Consoles.usuario.dto.DadosDetalhamentoUsuario;
import historia_consoles.backend_Consoles.usuario.dto.DadosLoginResposta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autenticacao")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final UsuarioService usuarioService;
    private final AutenticacaoService service;

    @PostMapping("/login")
    public ResponseEntity<DadosLoginResposta> efetuarLogin(@RequestBody DadosAutenticacao dados){
        var resposta = service.autenticar(dados);
        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/registro")
    public ResponseEntity<DadosDetalhamentoUsuario> registrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        var resposta = usuarioService.registrarUsuario(dados);
        return ResponseEntity.ok(new DadosDetalhamentoUsuario(resposta));
    }

}

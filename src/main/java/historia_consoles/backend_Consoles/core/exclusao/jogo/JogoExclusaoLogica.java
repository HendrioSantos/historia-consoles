package historia_consoles.backend_Consoles.core.exclusao.jogo;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.repository.JogoRepository;
import org.springframework.stereotype.Component;

@Component
public class JogoExclusaoLogica implements JogoMetodoExclusao {

    @Override
    public void JogoExcluir(Long id, JogoRepository repository) {
        var jogo = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new InvalidoException("Não encontrei o jogo"));
        jogo.setAtivo(false);
        repository.save(jogo);
    }

    @Override
    public boolean exclusaoLogica(boolean logico) {
        return logico;
    }
}

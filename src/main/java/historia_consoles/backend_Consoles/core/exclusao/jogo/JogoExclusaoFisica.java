package historia_consoles.backend_Consoles.core.exclusao.jogo;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.repository.JogoRepository;
import org.springframework.stereotype.Component;

@Component
public class JogoExclusaoFisica implements JogoMetodoExclusao {

    @Override
    public void JogoExcluir(Long id, JogoRepository repository) {
        if (repository.findByIdAndAtivoTrue(id).isEmpty()){
            throw new InvalidoException("Não encontrei o jogo");
        }
        repository.deleteById(id);
    }

    @Override
    public boolean exclusaoLogica(boolean logico) {
        return !logico;
    }
}

package historia_consoles.backend_Consoles.core.exclusao.geracao;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import org.springframework.stereotype.Component;

@Component
public class GeracaoExclusaoFisica implements GeracaoExclusao{

    @Override
    public void encontrarGeracao(Long id, GeracaoRepository repository) {
        if (repository.findByIdAndAtivoTrue(id).isEmpty()){
            throw new InvalidoException("Não encontrei a geração");
        }
        repository.deleteById(id);
    }

    @Override
    public boolean exclusaoLogica(boolean logico) {
        return !logico;
    }
}

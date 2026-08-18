package historia_consoles.backend_Consoles.core.exclusao.geracao;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;
import org.springframework.stereotype.Component;

@Component
public class GeracaoExclusaoLogica implements GeracaoExclusao{

    @Override
    public void encontrarGeracao(Long id, GeracaoRepository repository) {
        var geracao = repository.findByIdAndAtivoTrue(id).orElseThrow(() -> new InvalidoException("Não foi encontrado a geração"));
        geracao.setAtivo(false);
        repository.save(geracao);
    }

    @Override
    public boolean exclusaoLogica(boolean logico) {
        return logico;
    }
}

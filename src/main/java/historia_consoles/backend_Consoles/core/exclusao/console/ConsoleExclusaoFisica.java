package historia_consoles.backend_Consoles.core.exclusao.console;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;
import org.springframework.stereotype.Component;

@Component
public class ConsoleExclusaoFisica implements ConsoleExclusao{

    @Override
    public void consoleExcluir(Long id, ConsoleRepository repository) {
        var console = repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new InvalidoException("Não foi encontrado nenhum console"));
        repository.delete(console);
    }

    @Override
    public boolean exclusaoLogica(boolean logico) {
        return !logico;
    }
}

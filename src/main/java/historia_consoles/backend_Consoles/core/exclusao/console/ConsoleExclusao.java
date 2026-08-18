package historia_consoles.backend_Consoles.core.exclusao.console;

import historia_consoles.backend_Consoles.core.repository.ConsoleRepository;

public interface ConsoleExclusao {
    void consoleExcluir(Long id, ConsoleRepository repository);
    boolean exclusaoLogica(boolean logico);
}

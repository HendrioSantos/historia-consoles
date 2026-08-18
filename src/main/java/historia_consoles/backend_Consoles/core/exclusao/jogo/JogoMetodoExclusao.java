package historia_consoles.backend_Consoles.core.exclusao.jogo;

import historia_consoles.backend_Consoles.core.repository.JogoRepository;

public interface JogoMetodoExclusao {
    void JogoExcluir(Long id, JogoRepository repository);
    boolean exclusaoLogica(boolean logico);
}

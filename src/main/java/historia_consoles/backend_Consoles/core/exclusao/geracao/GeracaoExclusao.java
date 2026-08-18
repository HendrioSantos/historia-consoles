package historia_consoles.backend_Consoles.core.exclusao.geracao;

import historia_consoles.backend_Consoles.core.repository.GeracaoRepository;

public interface GeracaoExclusao {
    void encontrarGeracao(Long id, GeracaoRepository repository);
    boolean exclusaoLogica(boolean logico);
}

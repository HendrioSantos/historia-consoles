package historia_consoles.backend_Consoles.core.repository;

import historia_consoles.backend_Consoles.core.models.entities.Jogo;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

// aqui poderia ter a @Repository, porém a spring data jpa já faz isso em tempo de execução
public interface JogoRepository extends JpaRepository<Jogo, Long> {
    @EntityGraph(attributePaths = {"console", "console.geracao"})
    Optional<Jogo> findByIdAndAtivoTrue(Long id);
    Page<Jogo> findAllByAtivoTrue(Pageable paginacao);
    Optional<Jogo> findBySlugAndAtivoTrue(String slug);
    Page<Jogo> findAllByConsoleIdAndAtivoTrue(Long consoleId, Pageable paginacao);

    @Query(value = "SELECT j FROM Jogo j LEFT JOIN FETCH j.console WHERE j.ativo = true",
            countQuery = "SELECT count(j) FROM Jogo j WHERE j.ativo = true")
    Page<Jogo> findAllByAtivoTrueComFetchJoin(Pageable paginacao);

    Page<Jogo> findAllByJogoGenero(JogoGenero jogoGenero, Pageable pagina);

    Page<Jogo> findAllByConsoleIdAndJogoStatus(Long id, JogoStatus jogoStatus, Pageable pagina);

    Page<Jogo> findAllByNotaCriticaGreaterThanEqual(int notaCritica, Pageable pagina);

    Page<Jogo> findAllByRetrocompatibilidadeTrue(Pageable pagina);

}

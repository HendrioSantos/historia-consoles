package historia_consoles.backend_Consoles.core.repository;

import historia_consoles.backend_Consoles.core.models.entities.Console;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsoleRepository extends JpaRepository<Console, Long> {
    Optional<Console> findByIdAndAtivoTrue(Long consoleId);
    Page<Console> findAllByAtivoTrue(Pageable paginacao);
    Optional<Console> findBySlugIgnoreCaseAndAtivoTrue(String slug);
    @EntityGraph(attributePaths = {"geracao", "jogos"})
    Page<Console> findAllByGeracaoIdAndAtivoTrue(Long geracaoId, Pageable paginacao);
}

package historia_consoles.backend_Consoles.core.repository;

import historia_consoles.backend_Consoles.core.models.entities.Geracao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeracaoRepository extends JpaRepository<Geracao, Long> {
    Optional<Geracao> findByIdAndAtivoTrue(Long id);
    Optional<Geracao> findBySlugIgnoreCaseAndAtivoTrue(String slug);
    Page<Geracao> findAllByAtivoTrue(Pageable paginacao);
    Optional<Geracao> findByAtualTrue();
}

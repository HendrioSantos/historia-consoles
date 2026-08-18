package historia_consoles.backend_Consoles.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    UserDetails findByLogin(String login);
    Optional<Usuario> findByIdAndAtivoTrue(Long id);

    Optional<Usuario> findByLoginAndAtivoTrue(String login);
}

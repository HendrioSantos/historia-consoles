package historia_consoles.backend_Consoles.usuario;

import historia_consoles.backend_Consoles.usuario.dto.AtualizarUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String login;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    public Usuario(String login, String senha, Role role, boolean ativo) {
        this.login = login;
        this.senha = senha;
        this.role = role;
        this.ativo = ativo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    public void excluir() {
        ativo = false;
    }

    public void atualizarInformacoes(AtualizarUsuario dados) {
        if (dados.login() != null){
            this.login = dados.login();
        }
        if (dados.senha() != null){
            this.senha = dados.senha();
        }
        if (dados.role() != null){
            this.role = dados.role();
        }
        if (!dados.ativo()){
            this.ativo = !dados.ativo();
        }
    }
}

package historia_consoles.backend_Consoles.core.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import historia_consoles.backend_Consoles.core.dto.jogo.JogoAtualizar;
import historia_consoles.backend_Consoles.core.models.EntidadeAuditavel;
import historia_consoles.backend_Consoles.core.models.enums.JogoGenero;
import historia_consoles.backend_Consoles.core.models.enums.JogoMidiaOriginal;
import historia_consoles.backend_Consoles.core.models.enums.JogoStatus;
import historia_consoles.backend_Consoles.core.models.enums.JogoModo;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Entity
@Table(name = "jogos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited @AuditTable(value = "jogo_aud")
public class Jogo extends EntidadeAuditavel {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 1, message = "Nome é obrigatório")
    private String nome;
    @Column(nullable = false)
    @Size(min = 1, message = "Desenvolvedora é obrigatório")
    private String desenvolvedora;
    @Column(nullable = false)
    @Size(min = 1, message = "Publicadora é obrigatório")
    private String publicadora;
    @Column(nullable = false, name = "tamanho_arquivo")
    @Size(min = 1, message = "Este é o tamanho de jogo, tipo 64KB, é obrigatório")
    private String tamanhoArquivo;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "modo_jogo", columnDefinition = "TEXT")
    private JogoModo jogoModo;
    @Column(columnDefinition = "TEXT")
    private String imagemUrl;

    @Column(name = "url_video", columnDefinition = "TEXT")
    private String urlVideo;

    @Column(nullable = false, name = "diretor_criador")
    private String diretorCriador;

    @Column(nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "console_id")
    private Console console;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "jogogenero")
    private JogoGenero jogoGenero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "jogo_status")
    private JogoStatus jogoStatus = JogoStatus.ENTRADA_SISTEMA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "jogo_midia_original")
    private JogoMidiaOriginal midiaOriginal;

    @Column(nullable = false, name = "nota_critica")
    @PositiveOrZero(message = "Positivo ou zero")
    @Max(value = 100, message = "Nota do jogo é obrigatória")
    private Integer notaCritica;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean retrocompatibilidade = false;

    public void atualizarJogo(JogoAtualizar dto) {
        if (dto.nome() != null && !dto.nome().isBlank()) {
            this.nome = dto.nome();
            this.slug = FiltradorSlug.gerarSlug(dto.nome());
        }

        if (dto.desenvolvedora() != null && !dto.desenvolvedora().isBlank()) {
            this.desenvolvedora = dto.desenvolvedora();
        }
        if (dto.publicadora() != null && !dto.publicadora().isBlank()) {
            this.publicadora = dto.publicadora();
        }
        if (dto.tamanhoArquivo() != null && !dto.tamanhoArquivo().isBlank()) {
            this.tamanhoArquivo = dto.tamanhoArquivo();
        }
        if (dto.urlVideo() != null && !dto.urlVideo().isBlank()) {
            this.urlVideo = dto.urlVideo();
        }
        if (dto.diretorCriador() != null && !dto.diretorCriador().isBlank()) {
            this.diretorCriador = dto.diretorCriador();
        }

        if (dto.imagemUrl() != null && !dto.imagemUrl().isBlank()){
            this.imagemUrl = dto.imagemUrl();
        }

        if (dto.jogoModo() != null) {
            this.jogoModo = dto.jogoModo();
        }
        if (dto.jogoGenero() != null) {
            this.jogoGenero = dto.jogoGenero();
        }
        if (dto.jogoStatus() != null) {
            this.jogoStatus = dto.jogoStatus();
        }
        if (dto.midiaOriginal() != null) {
            this.midiaOriginal = dto.midiaOriginal();
        }
        if (dto.notaCritica() != null) {
            this.notaCritica = dto.notaCritica();
        }

        this.ativo = dto.ativo();
        this.retrocompatibilidade = dto.retrocompatibilidade();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Jogo jogo)) return false;
        return Objects.equals(getId(), jogo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Jogo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", desenvolvedora='" + desenvolvedora + '\'' +
                ", publicadora='" + publicadora + '\'' +
                ", ativo=" + ativo +
                '}';
    }
}


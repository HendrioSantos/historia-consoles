package historia_consoles.backend_Consoles.core.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.dto.console.ConsoleAtualizar;
import historia_consoles.backend_Consoles.core.models.EntidadeAuditavel;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "consoles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
@AuditTable(value = "console_aud")
public class Console extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;
    @Column(nullable = false)
    private String fabricante;
    @Column(nullable = false)
    private String publicadora;
    @Column(nullable = false, unique = true)
    private String slug;
    private String imagemUrl;
    @Column(nullable = false)
    private String unidadesVendidas;

    @Embedded
    @Valid
    private Hardware hardware;

    @Embedded
    @Valid
    private Periodo periodo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geracao_id")
    private Geracao geracao;

    @OneToMany(mappedBy = "console")
    private List<Jogo> jogos;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;
    @Builder.Default
    @Column(nullable = false)
    private boolean descontinuado = true;
    @Builder.Default
    @Column(nullable = false)
    private boolean retrocompatibilidade = false;


    public void atualizar(ConsoleAtualizar dados, String novoSlug, String novaImagem) {
        if (dados.nome() != null && !dados.nome().isBlank()) this.nome = dados.nome();
        if (dados.fabricante() != null && !dados.fabricante().isBlank()) this.fabricante = dados.fabricante();
        if (dados.publicadora() != null && !dados.publicadora().isBlank()) this.publicadora = dados.publicadora();
        if (dados.unidadesVendidas() != null && !dados.unidadesVendidas().isBlank()) this.unidadesVendidas = dados.unidadesVendidas();

        if (novoSlug != null && !novoSlug.isBlank()) this.slug = novoSlug;
        if (novaImagem != null && !novaImagem.isBlank()) this.imagemUrl = novaImagem;

        this.ativo = dados.ativo();
        this.descontinuado = dados.descontinuado();
        this.retrocompatibilidade = dados.retrocompatibilidade();

        if (dados.hardware() != null) {
            this.hardware = mesclarHardware(dados.hardware());
        }

        if (dados.periodo() != null) {
            Periodo periodoMesclado = mesclarPeriodo(dados.periodo());
            if (!periodoMesclado.isPeriodoValido()) {
                throw new InvalidoException("A data de início do período não pode ser posterior à data de fim.");
            }
            this.periodo = periodoMesclado;
        }
    }

    private Hardware mesclarHardware(Hardware novo) {
        if (this.hardware == null) return novo;
        return this.hardware.toBuilder()
                .cpu(novo.getCpu() != null ? novo.getCpu() : this.hardware.getCpu())
                .gpu(novo.getGpu() != null ? novo.getGpu() : this.hardware.getGpu())
                .ram(novo.getRam() != null ? novo.getRam() : this.hardware.getRam())
                .armazenamento(novo.getArmazenamento() != null ? novo.getArmazenamento() : this.hardware.getArmazenamento())
                .midia(novo.getMidia() != null ? novo.getMidia() : this.hardware.getMidia())
                .resolucao(novo.getResolucao() != null ? novo.getResolucao() : this.hardware.getResolucao())
                .consoleTipo(novo.getConsoleTipo() != null ? novo.getConsoleTipo() : this.hardware.getConsoleTipo())
                .precoLancamento(novo.getPrecoLancamento() != null ? novo.getPrecoLancamento() : this.hardware.getPrecoLancamento())
                .build();
    }

    private Periodo mesclarPeriodo(Periodo novo) {
        if (this.periodo == null) return novo;
        return this.periodo.toBuilder()
                .inicio(novo.getInicio() != null ? novo.getInicio() : this.periodo.getInicio())
                .fim(novo.getFim() != null ? novo.getFim() : this.periodo.getFim())
                .build();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Console console)) return false;
        return Objects.equals(getId(), console.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Console{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", fabricante='" + fabricante + '\'' +
                ", publicadora='" + publicadora + '\'' +
                ", ativo=" + ativo +
                ", descontinuado=" + descontinuado +
                ", slug='" + slug + '\'' +
                ", imagemUrl='" + imagemUrl + '\'' +
                '}';
    }
}

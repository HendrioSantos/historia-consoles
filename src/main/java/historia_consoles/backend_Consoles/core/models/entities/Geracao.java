package historia_consoles.backend_Consoles.core.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import historia_consoles.backend_Consoles.core.dto.geracao.GeracaoAtualizar;
import historia_consoles.backend_Consoles.core.models.EntidadeAuditavel;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoEmpresaDominante;
import historia_consoles.backend_Consoles.filtradores.FiltradorSlug;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Entity
@Table(name = "geracoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Audited
@AuditTable(value = "geracao_aud")
public class Geracao extends EntidadeAuditavel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, name = "fato_historico", columnDefinition = "TEXT")
    private String fatoHistorico;

    @Column(nullable = false, unique = true)
    private String slug;

    @Min(value = 1, message = "O número da geração não pode ser menor que 1")
    @Max(value = 10, message = "O número da geração não pode ser maior que 10")
    @Column(nullable = false, name = "numero_geracao")
    private int numeroGeracao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "geracao_cronologia")
    private GeracaoCronologia geracaoCronologia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "geracao_empresa_dominante")
    private GeracaoEmpresaDominante geracaoEmpresaDominante;

    @Embedded
    @Valid
    private Periodo periodo;

    @Builder.Default
    @Column(nullable = false)
    private boolean atual = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    public void atualizarInformacoes(GeracaoAtualizar dados, FiltradorSlug slugger) {
        if (dados.nome() != null && !dados.nome().isBlank()) {
            this.nome = dados.nome();
            this.slug = FiltradorSlug.gerarSlug(dados.nome());
        }

        if (dados.numeroGeracao() >= 1 && dados.numeroGeracao() <= 10) {
            this.numeroGeracao = dados.numeroGeracao();
        }

        if (dados.cronologia() != null) {
            this.geracaoCronologia = dados.cronologia();
        }
        this.atual = dados.atual();
        this.ativo = dados.ativo();

        if (dados.fatoHistorico() != null && !dados.fatoHistorico().isBlank()) {
            this.fatoHistorico = dados.fatoHistorico();
        }

        if (dados.geracaoEmpresaDominante() != null) {
            this.geracaoEmpresaDominante = dados.geracaoEmpresaDominante();
        }

        if (dados.periodo() != null) {
            this.periodo = dados.periodo();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Geracao geracao)) return false;
        return Objects.equals(getId(), geracao.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Geracao{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", atual=" + atual +
                '}';
    }
}

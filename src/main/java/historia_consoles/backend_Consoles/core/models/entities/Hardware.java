package historia_consoles.backend_Consoles.core.models.entities;

import historia_consoles.backend_Consoles.core.models.enums.ConsoleTipo;
import jakarta.persistence.*;
import lombok.*;

@Embeddable@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Hardware {
    private String cpu;
    private String gpu;
    private String ram;
    private String armazenamento;
    private String midia;
    private String resolucao;
    @Column(name = "preco_lancamento")
    private String precoLancamento;
    @Enumerated(EnumType.STRING)
    @Column(name = "console_tipo")
    private ConsoleTipo consoleTipo;
}

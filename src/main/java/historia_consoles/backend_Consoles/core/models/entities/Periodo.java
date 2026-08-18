package historia_consoles.backend_Consoles.core.models.entities;

import historia_consoles.backend_Consoles.common.InvalidoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Periodo {

    @NotNull
    @Column(name = "inicio_periodo")
    private LocalDate inicio;

    @Column(name = "fim_periodo")
    private LocalDate fim;

    public boolean isPeriodoValido() throws InvalidoException {
        if (inicio == null){
            return false;
        }
        if (fim == null){
            return true;
        }
        return !inicio.isAfter(fim);

    }

}

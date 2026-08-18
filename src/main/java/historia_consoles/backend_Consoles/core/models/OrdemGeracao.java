package historia_consoles.backend_Consoles.core.models;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.core.models.enums.GeracaoCronologia;
import org.springframework.http.HttpStatus;

public record OrdemGeracao(
        Integer valor
) {
    public OrdemGeracao {
        if (valor == null || valor < 1 || valor > 10){
            throw new InvalidoException(
                    "Somente valores de 1 ao 10",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public GeracaoCronologia obterCronologia() {
        return GeracaoCronologia.verificarCronologiaGeracao(this.valor);
    }
}

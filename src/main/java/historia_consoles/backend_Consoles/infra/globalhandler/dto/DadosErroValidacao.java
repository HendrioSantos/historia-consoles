package historia_consoles.backend_Consoles.infra.globalhandler.dto;

import org.springframework.validation.FieldError;

public record DadosErroValidacao(
        String campo,
        String mensagem
) {
    public DadosErroValidacao(FieldError error) {
        this(error.getField(), error.getDefaultMessage());
    }
}

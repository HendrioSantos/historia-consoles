package historia_consoles.backend_Consoles.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidoException extends RuntimeException {

    private final HttpStatus status;

    public InvalidoException(String message, HttpStatus status) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public InvalidoException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public InvalidoException(HttpStatus status) {
        super();
        this.status = status;
    }
}

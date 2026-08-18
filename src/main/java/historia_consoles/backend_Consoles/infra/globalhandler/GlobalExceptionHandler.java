package historia_consoles.backend_Consoles.infra.globalhandler;

import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.infra.globalhandler.dto.DadosErroSimples;
import historia_consoles.backend_Consoles.infra.globalhandler.dto.DadosErroValidacao;
import historia_consoles.backend_Consoles.infra.globalhandler.dto.DadosErroSimplesCaminho;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidoException.class)
    public ResponseEntity<DadosErroSimples> tratarRegraDeNegocio(InvalidoException ex) {
        log.warn("Regra de negócio violada. Status: {} - Motivo: {}", ex.getStatus(), ex.getMessage());
        if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("token")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new DadosErroSimples(ex.getMessage(), LocalDateTime.now()));
        }
        return ResponseEntity.status(ex.getStatus()).body(new DadosErroSimples(ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> tratarErroValidacao(MethodArgumentNotValidException ex) {
        var erros = ex.getFieldErrors().stream().map(DadosErroValidacao::new).toList();
        log.debug("Falha de validação de campos de entrada. Quantidade de erros: {}", erros.size());
        return ResponseEntity.badRequest().body(erros);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DadosErroSimples> tratarBadCredentialsException(BadCredentialsException e){
        log.error("Credenciais/Senha incorreta {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DadosErroSimples("Senha incorreta", LocalDateTime.now()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<DadosErroSimples> tratarNoResourceFoundException(NoResourceFoundException e) {
        log.warn("Tentativa de acesso a rota ou recurso inexistente: {}", e.getResourcePath());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErroSimples(
                "A rota ou recurso solicitado nao foi encontrado", LocalDateTime.now()));
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DadosErroSimples> tratar404(EntityNotFoundException e){
        log.debug("Recurso solicitado não foi encontrado no banco de dados (404)");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErroSimples("Recurso não encontrado", LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErroSimples> tratarErroLeitura(HttpMessageNotReadableException ex) {
        log.warn("Falha na leitura do payload HTTP", ex);
        return ResponseEntity.badRequest()
                .body(new DadosErroSimples("Corpo requisição inválido, mal formatado ou com dados ausentes", LocalDateTime.now()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<DadosErroSimples> tratarParametroAusente(MissingServletRequestParameterException ex) {
        log.warn("Requisição rejeitada por falta de parâmetro obrigatório: {}", ex.getParameterName());
        return ResponseEntity.badRequest().body(new DadosErroSimples(
                "Parâmetro obrigatório ausente: " + ex.getParameterName(), LocalDateTime.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<DadosErroSimples> tratarErroIntegridade(DataIntegrityViolationException ex) {
        log.error("Violação de integridade com o banco de dados. Operação abortada", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new DadosErroSimples(
                        "Não foi possível realizar esta operação devido a uma restrição de integridade dos dados.", LocalDateTime.now()));
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<DadosErroSimples> tratarErroReferenciaDePropriedade(PropertyReferenceException e){
        log.error("Erro de propriedade ao enviar o json: ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new DadosErroSimples("Dado enviado na requisição ou uma falha na sintaxe/parâmetros",LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<DadosErroSimplesCaminho> tratarErroDeEstruturaBanco(InvalidDataAccessResourceUsageException ex, HttpServletRequest request) {
        log.error("Erro crítico de consistência entre a Entidade Java e a tabela do Banco de Dados: {}", ex.getMessage());

        var erro = new DadosErroSimplesCaminho(
                "O servidor encontrou uma inconsistência nos dados internos. Por favor, contate o suporte", LocalDateTime.now(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosErroSimples> tratarErro500(Exception e){
        log.error("Erro crítico não mapeado capturado pelo handler global: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DadosErroSimples("Ocorreu um erro interno inesperado no servidor. Tente novamente mais tarde.",
                        LocalDateTime.now()));
    }
}

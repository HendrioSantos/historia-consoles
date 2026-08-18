package historia_consoles.backend_Consoles.validadores;

import historia_consoles.backend_Consoles.common.InvalidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ValidadorImagemUrl {

    public String validarImagemUrl(String url) {
        log.debug("Executando validação para a URL da imagem: {}", url);

        boolean ehValido = url.startsWith("http://") || url.startsWith("https://");
        if (!ehValido) {
            throw new InvalidoException("A URL da imagem deve ser http ou https");
        }
        log.debug("URL validada com sucesso");
        return url;
    }

}

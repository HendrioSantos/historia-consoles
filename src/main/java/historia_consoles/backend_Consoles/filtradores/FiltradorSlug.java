package historia_consoles.backend_Consoles.filtradores;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FiltradorSlug {

    public static String gerarSlug(String nome) {
        if (nome == null) {
            log.debug("Tentativa de gerar slug para valor nulo. Retornando null.");
            return null;
        }
        log.debug("Gerando slug para o texto original: '{}'", nome);
        var slug = nome.toLowerCase()
                .replaceAll("[áàâãä]", "a")
                .replaceAll("[éèêë]", "e")
                .replaceAll("[íìîï]", "i")
                .replaceAll("[óòôõö]", "o")
                .replaceAll("[úùûü]", "u")
                .replaceAll("ç", "c")
                .replaceAll("[^a-z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-");
        log.debug("Slug gerado com sucesso: '{}'", slug);
        return nome;
    }

}

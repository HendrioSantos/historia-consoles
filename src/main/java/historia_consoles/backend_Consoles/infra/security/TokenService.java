package historia_consoles.backend_Consoles.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import historia_consoles.backend_Consoles.common.InvalidoException;
import historia_consoles.backend_Consoles.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario){
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("Api de história dos consoles")
                    .withSubject(usuario.getLogin())
                    .withExpiresAt(expiracaoData())
                    .sign(algoritmo);
        } catch (JWTCreationException e){
            throw new InvalidoException("Erro ao gerar token: " + e.getLocalizedMessage());
        }
    }

    public String validarToken(String token){
        try {
            Algorithm algoritmo = Algorithm.HMAC256(secret);
            return JWT.require(algoritmo)
                    .withIssuer("Api de história dos consoles")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e){
            throw new InvalidoException("Erro ao validar o token: " + e.getLocalizedMessage());
        }
    }

    private Instant expiracaoData() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

}

package historia_consoles.backend_Consoles.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // --- CORS
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // --- LIBERAÇÃO PÚBLICA ---
                        .requestMatchers(HttpMethod.POST, "/autenticacao/login").permitAll()
                        // --- REGISTRO E LIBERAÇÃO PÚBLICA
                        .requestMatchers(HttpMethod.POST, "/autenticacao/registro").permitAll()
                        // --- LIBERAÇÃO PÚBLICA DO SWAGGER E AUTENTICAÇÃO ---
                        .requestMatchers(
                                "/autenticacao/login",
                                "/autenticacao/registro",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // --- ADMIN ---
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // --- GERAÇÃO ---
                        .requestMatchers(HttpMethod.POST, "/geracao/**").hasAnyRole("ADMIN", "CARGO_GERACAO")
                        .requestMatchers(HttpMethod.PUT, "/geracao/**").hasAnyRole("ADMIN", "CARGO_GERACAO")
                        .requestMatchers(HttpMethod.DELETE, "/geracao/**").hasAnyRole("ADMIN", "CARGO_GERACAO")

                        // --- CONSOLE ---
                        .requestMatchers(HttpMethod.POST, "/console/**").hasAnyRole("ADMIN", "CARGO_CONSOLE")
                        .requestMatchers(HttpMethod.PUT, "/console/**").hasAnyRole("ADMIN", "CARGO_CONSOLE")
                        .requestMatchers(HttpMethod.DELETE, "/console/**").hasAnyRole("ADMIN", "CARGO_CONSOLE")

                        // --- JOGO ---
                        .requestMatchers(HttpMethod.POST, "/jogo/**").hasAnyRole("ADMIN", "CARGO_JOGO")
                        .requestMatchers(HttpMethod.PUT, "/jogo/**").hasAnyRole("ADMIN", "CARGO_JOGO")
                        .requestMatchers(HttpMethod.DELETE, "/jogo/**").hasAnyRole("ADMIN", "CARGO_JOGO")

                        // --- LEITOR ---
                        .requestMatchers(HttpMethod.GET, "/**")
                        .hasAnyRole("LEITOR", "ADMIN", "CARGO_GERACAO", "CARGO_CONSOLE", "CARGO_JOGO")

                        // --- QUALQUER OUTRA REQUISIÇÃO PRECISA DE AUTENTICAÇÃO ---
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200", "http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

}

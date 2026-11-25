package co.com.wallacesoft.paymenow.config;

import co.com.wallacesoft.paymenow.exception.ApiError;
import co.com.wallacesoft.paymenow.repository.UsuarioRepository;
import co.com.wallacesoft.paymenow.security.FirebaseAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Permisos por rol. Lo que no aparece aqui queda solo para ADMIN.
 * Que registros puede ver o cambiar un CLIENTE se valida en cada service.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";

    private final FirebaseAuth firebaseAuth;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Sin sesiones ni cookies: cada peticion trae su token, por eso no aplica CSRF
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((request, response, ex) -> escribirError(request, response,
                                HttpStatus.UNAUTHORIZED, "Debes iniciar sesion para realizar esta accion."))
                        .accessDeniedHandler((request, response, ex) -> escribirError(request, response,
                                HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta accion.")))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Publico: lo necesita la pantalla de registro
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tipos-identificacion").permitAll()

                        // ADMIN y CLIENTE
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/plataformas", "/api/v1/plataformas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/personas", "/api/v1/personas/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cuentas", "/api/v1/cuentas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/cuentas").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cuentas/*").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cuentas-asociadas", "/api/v1/cuentas-asociadas/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/cuentas-asociadas/por-identificacion").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cuentas-asociadas/*").authenticated()

                        .anyRequest().hasRole(ADMIN))
                // Se crea aqui y no como @Component para que Spring Boot no lo registre dos veces
                .addFilterBefore(new FirebaseAuthFilter(firebaseAuth, usuarioRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void escribirError(HttpServletRequest request, HttpServletResponse response,
                               HttpStatus status, String mensaje) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        objectMapper.writeValue(response.getOutputStream(),
                new ApiError(status.value(), status.getReasonPhrase(), mensaje, request.getRequestURI()));
    }
}

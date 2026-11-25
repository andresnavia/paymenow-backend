package co.com.wallacesoft.paymenow.security;

import co.com.wallacesoft.paymenow.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Valida el token de Firebase y busca el usuario activo que le corresponde.
 * Si algo falla la peticion sigue sin autenticar, y SecurityConfig decide si la rechaza.
 */
@RequiredArgsConstructor
@Slf4j
public class FirebaseAuthFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final FirebaseAuth firebaseAuth;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String cabecera = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (cabecera != null && cabecera.startsWith(PREFIJO_BEARER)) {
            autenticar(cabecera.substring(PREFIJO_BEARER.length()).trim());
        }
        chain.doFilter(request, response);
    }

    private void autenticar(String token) {
        String uid;
        try {
            uid = firebaseAuth.verifyIdToken(token).getUid();
        } catch (FirebaseAuthException | IllegalArgumentException e) {
            log.debug("Token de Firebase rechazado: {}", e.getMessage());
            return;
        }

        List<UsuarioAutenticado> usuarios = usuarioRepository.findActivosByIdFirebase(uid);
        if (usuarios.size() != 1) {
            log.warn("Se esperaba un usuario activo para el uid={} y hay {}", uid, usuarios.size());
            return;
        }

        UsuarioAutenticado usuario = usuarios.get(0);
        String rol = usuario.esAdmin() ? "ROLE_ADMIN" : usuario.esCliente() ? "ROLE_CLIENTE" : null;
        if (rol == null) {
            log.warn("El usuario {} tiene un rol no soportado: {}", usuario.idUsua(), usuario.idRol());
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                usuario, null, List.of(new SimpleGrantedAuthority(rol))));
    }
}

package co.com.wallacesoft.paymenow.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/** Da acceso a los services al usuario autenticado en la peticion actual. */
@Component
public class UsuarioActual {

    public UsuarioAutenticado get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioAutenticado usuario) {
            return usuario;
        }
        throw new AuthenticationCredentialsNotFoundException("No hay un usuario autenticado.");
    }

    public boolean esAdmin() {
        return get().esAdmin();
    }

    public Integer idPersona() {
        return get().idPers();
    }
}

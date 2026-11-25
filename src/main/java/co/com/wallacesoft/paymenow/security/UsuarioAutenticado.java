package co.com.wallacesoft.paymenow.security;

import co.com.wallacesoft.paymenow.constants.Roles;

/** Datos del usuario que hizo la peticion, obtenidos a partir de su token de Firebase. */
public record UsuarioAutenticado(Integer idUsua, Integer idPers, Integer idRol) {

    public boolean esAdmin() {
        return idRol != null && idRol == Roles.ADMIN;
    }

    public boolean esCliente() {
        return idRol != null && idRol == Roles.CLIENTE;
    }
}

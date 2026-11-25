package co.com.wallacesoft.paymenow.dto;

/** Datos del usuario autenticado que el frontend necesita para adaptar la pantalla. */
public record PerfilDTO(
        Integer idPers,
        Integer idRol,
        String nombreRol,
        String nombreCompleto,
        boolean admin
) {
}

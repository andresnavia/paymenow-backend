package co.com.wallacesoft.paymenow.dto;

/** Lo minimo para asociar a una persona a una cuenta, sin exponer sus datos personales. */
public record PersonaBasicaDTO(
        String nombreCompleto
) {
}

package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Asocia a una persona a una cuenta usando su identificacion, sin conocer su id interno. */
public record AsociarPersonaDTO(
        @NotNull(message = "La cuenta es obligatoria")
        Integer idCuen,

        @NotBlank(message = "La identificacion es obligatoria")
        @Size(max = 20)
        String identificacion,

        @Pattern(regexp = "[SN]", message = "Activo debe ser 'S' o 'N'")
        String activo,

        @Pattern(regexp = "[SN]", message = "Notifica debe ser 'S' o 'N'")
        String notifica
) {
}

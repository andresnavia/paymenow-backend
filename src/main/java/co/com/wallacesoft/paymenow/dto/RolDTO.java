package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RolDTO(
        Integer idRol,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50)
        String nombre,

        @Size(max = 500)
        String descripcion,

        @Pattern(regexp = "[SN]", message = "Activo debe ser 'S' o 'N'")
        String activo,

        LocalDate fechaCreacion
) {
}

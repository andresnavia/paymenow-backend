package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UsuarioDTO(
        Integer idUsua,

        @NotNull(message = "La persona es obligatoria")
        Integer idPers,

        @NotNull(message = "El rol es obligatorio")
        Integer idRol,

        @NotBlank(message = "El id de Firebase es obligatorio")
        @Size(max = 200)
        String idFirebase,

        @Pattern(regexp = "[SN]", message = "Activo debe ser 'S' o 'N'")
        String activo,

        LocalDate fechaCreacion
) {
}

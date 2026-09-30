package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TiposIdentificacionDTO(
        Integer idTiid,

        @NotBlank(message = "La abreviatura es obligatoria")
        @Size(max = 10, message = "La abreviatura no puede superar 10 caracteres")
        String abreviatura,

        @Size(max = 200, message = "La descripcion no puede superar 200 caracteres")
        String descripcion,

        @Pattern(regexp = "[SN]", message = "Activo debe ser 'S' o 'N'")
        String activo,

        LocalDate fechaCreacion
) {
}

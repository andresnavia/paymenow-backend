package co.com.wallacesoft.paymenow.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CuentaAsociadaDTO(
        Integer idCuas,

        @NotNull(message = "La cuenta es obligatoria")
        Integer idCuen,

        @NotNull(message = "La persona es obligatoria")
        Integer idPers,

        @Pattern(regexp = "[SN]", message = "Activo debe ser 'S' o 'N'")
        String activo,

        @Pattern(regexp = "[SN]", message = "Notifica debe ser 'S' o 'N'")
        String notifica,

        LocalDate fechaCreacion
) {
}

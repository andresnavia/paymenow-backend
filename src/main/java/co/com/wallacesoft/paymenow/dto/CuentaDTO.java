package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record CuentaDTO(
        Integer idCuen,

        @NotNull(message = "La plataforma es obligatoria")
        Integer idPlat,

        @NotNull(message = "El propietario (persona) es obligatorio")
        Integer idPers,

        @Pattern(regexp = "[SN]", message = "Activo debe ser 'S' o 'N'")
        String activo,

        @NotNull(message = "La fecha de pago es obligatoria")
        LocalDate fechaPago
) {
}

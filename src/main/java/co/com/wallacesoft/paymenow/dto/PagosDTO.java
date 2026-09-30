package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PagosDTO(
        Integer idPago,

        @NotNull(message = "La fecha de pago es obligatoria")
        LocalDate fechaPago,

        @NotNull(message = "La cuenta asociada es obligatoria")
        Integer idCuas,

        @NotNull(message = "El estado de pago es obligatorio")
        Integer idEspa,

        LocalDate fechaCreacion
) {
}

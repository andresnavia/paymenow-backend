package co.com.wallacesoft.paymenow.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstadosPagoDTO(
        Integer idEspa,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 45)
        String nombre,

        @Size(max = 200)
        String descripcion,

        LocalDate fechaCreacion
) {
}

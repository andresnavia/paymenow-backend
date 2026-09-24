package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.*;

public record PlataformaDTO(
        Integer idPlat,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60)
        String nombre,

        @Size(max = 200)
        String descripcion,

        @NotNull(message = "La cantidad de cuentas es obligatoria")
        @Min(value = 1, message = "La cantidad de cuentas debe ser al menos 1")
        Integer cantidadCuentas,

        @NotNull(message = "El valor es obligatorio")
        @Min(value = 0, message = "El valor no puede ser negativo")
        Integer valor
) {
}

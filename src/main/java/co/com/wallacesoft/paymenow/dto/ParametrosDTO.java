package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ParametrosDTO(
        Integer idPara,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 45)
        String nombre,

        @Size(max = 200)
        String descripcion,

        @NotBlank(message = "El valor es obligatorio")
        @Size(max = 1000)
        String valor
) {
}

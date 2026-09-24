package co.com.wallacesoft.paymenow.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PersonaDTO(
        Integer idPers,

        @NotBlank(message = "El primer nombre es obligatorio")
        @Size(max = 60)
        String primerNombre,

        @Size(max = 60)
        String segundoNombre,

        @NotBlank(message = "El primer apellido es obligatorio")
        @Size(max = 60)
        String primerApellido,

        @Size(max = 60)
        String segundoApellido,

        @NotBlank(message = "El sexo es obligatorio")
        @Size(min = 1, max = 1, message = "El sexo debe ser un solo caracter (M/F)")
        String sexo,

        @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
        LocalDate fechaNacimiento,

        @NotNull(message = "El tipo de identificacion es obligatorio")
        Integer idTiid,

        String abreviatura,

        @NotBlank(message = "La identificacion es obligatoria")
        @Size(max = 20)
        String identificacion,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        @Size(max = 70)
        String email
) {
}

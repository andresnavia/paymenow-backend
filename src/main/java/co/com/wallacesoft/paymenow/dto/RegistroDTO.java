package co.com.wallacesoft.paymenow.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record RegistroDTO (
        @NotBlank (message = "El primer nombre es obligatorio")
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

        @NotNull (message = "El tipo de identificacion es obligatorio")
        Integer idTiid,

        String abreviatura,

        @NotBlank(message = "La identificacion es obligatoria")
        @Size(max = 20)
        String identificacion,

        @NotBlank(message = "El email es obligatorio")
        @Email (message = "El email no tiene un formato valido")
        @Size(max = 70)
        String email,

        @Size(max = 20)
        String telefono,

        @Size(max = 20)
        String celular,

        @NotBlank(message = "El password es obligatorio")
        @Size(min=6 )
        String password
){

}

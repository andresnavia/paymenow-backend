package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "persona")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PERS")
    private Integer idPers;

    @Column(name = "PRIMER_NOMBRE", nullable = false, length = 60)
    private String primerNombre;

    @Column(name = "SEGUNDO_NOMBRE", length = 60)
    private String segundoNombre;

    @Column(name = "PRIMER_APELLIDO", nullable = false, length = 60)
    private String primerApellido;

    @Column(name = "SEGUNDO_APELLIDO", length = 60)
    private String segundoApellido;

    @Column(name = "SEXO", nullable = false, length = 1)
    private String sexo;

    @Column(name = "FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_TIID", nullable = false)
    private TiposIdentificacion tipoIdentificacion;

    @Column(name = "IDENTIFICACION", nullable = false, length = 20, unique = true)
    private String identificacion;

    @Column(name = "EMAIL", nullable = false, length = 70)
    private String email;
}

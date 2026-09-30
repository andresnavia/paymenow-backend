package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "plataforma")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plataforma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PLAT")
    private Integer idPlat;

    @Column(name = "NOMBRE", nullable = false, length = 60)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "CANTIDAD_CUENTAS", nullable = false)
    private Integer cantidadCuentas;

    @Column(name = "VALOR", nullable = false)
    private Integer valor;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

    @Column(name = "FECHA_CREACION", nullable = false, insertable = false, updatable = false)
    private LocalDate fechaCreacion;
}

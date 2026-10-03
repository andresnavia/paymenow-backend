package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "tipos_identificacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TiposIdentificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIID")
    private Integer idTiid;

    @Column(name = "ABREVIATURA", nullable = false, length = 10)
    private String abreviatura;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "FECHA_CREACION", nullable = false, insertable = false, updatable = false)
    private LocalDate fechaCreacion;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false, length = 1, columnDefinition = "CHAR(1)")
    private String activo = "S";
}

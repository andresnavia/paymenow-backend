package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

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
}

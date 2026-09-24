package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "estados_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadosPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ESPA")
    private Integer idEspa;

    @Column(name = "NOMBRE", nullable = false, length = 45)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;
}

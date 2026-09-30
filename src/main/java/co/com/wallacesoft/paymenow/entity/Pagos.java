package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pagos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PAGO")
    private Integer idPago;

    @Column(name = "FECHA_PAGO", nullable = false)
    private LocalDate fechaPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CUAS", nullable = false)
    private CuentaAsociada cuentaAsociada;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ESPA", nullable = false)
    private EstadosPago estadoPago;

    @Column(name = "FECHA_CREACION", nullable = false, insertable = false, updatable = false)
    private LocalDate fechaCreacion;

}

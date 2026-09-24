package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cuenta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CUEN")
    private Integer idCuen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PLAT", nullable = false)
    private Plataforma plataforma;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERS", nullable = false)
    private Persona propietario;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

    @Column(name = "FECHA_PAGO", nullable = false)
    private LocalDate fechaPago;
}

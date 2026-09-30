package co.com.wallacesoft.paymenow.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cuenta_asociada")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaAsociada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CUAS")
    private Integer idCuas;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CUEN", nullable = false)
    private Cuenta cuenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERS", nullable = false)
    private Persona persona;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

    @Builder.Default
    @Column(name = "NOTIFICA", nullable = false, length = 1)
    private String notifica = "S";

    @Column(name = "FECHA_CREACION", nullable = false, insertable = false, updatable = false)
    private LocalDate fechaCreacion;
}

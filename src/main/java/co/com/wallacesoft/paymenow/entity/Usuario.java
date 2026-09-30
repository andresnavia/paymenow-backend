package co.com.wallacesoft.paymenow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUA")
    private Integer idUsua;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_PERS", nullable = false)
    private Persona persona;

    @Column(name = "FECHA_CREACION", nullable = false, insertable = false, updatable = false)
    private LocalDate fechaCreacion;

    @Builder.Default
    @Column(name = "ACTIVO", nullable = false, length = 1)
    private String activo = "S";

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_ROL", nullable = false)
    private Rol rol;

    @Column(name = "ID_FIREBASE", nullable = false, length = 200)
    private String idFirebase;
}

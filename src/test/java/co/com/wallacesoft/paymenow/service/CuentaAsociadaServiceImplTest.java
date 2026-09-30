package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.CuentaAsociadaDTO;
import co.com.wallacesoft.paymenow.entity.Cuenta;
import co.com.wallacesoft.paymenow.entity.CuentaAsociada;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.Plataforma;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.CuentaAsociadaRepository;
import co.com.wallacesoft.paymenow.repository.CuentaRepository;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.service.impl.CuentaAsociadaServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaAsociadaServiceImplTest {

    @Mock
    private CuentaAsociadaRepository cuentaAsociadaRepository;

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CuentaAsociadaServiceImpl service;

    private Plataforma plataforma;
    private Cuenta cuenta;
    private Persona persona;

    @BeforeEach
    void setUp() {
        // Plataforma que solo permite 2 pantallas, para poder probar la regla de negocio facilmente
        plataforma = Plataforma.builder().idPlat(1).nombre("HBO").cantidadCuentas(2).valor(20000).build();
        cuenta = Cuenta.builder().idCuen(1).plataforma(plataforma)
                .propietario(Persona.builder().idPers(1).build())
                .activo("S").fechaPago(LocalDate.of(2026, 9, 20)).build();
        persona = Persona.builder().idPers(2).primerNombre("Ana").primerApellido("Gomez")
                .sexo("F").identificacion("456").email("ana@correo.com").build();
    }

    @Test
    void create_cuandoHayCupoDisponible_deberiaCrear() {
        CuentaAsociadaDTO dto = new CuentaAsociadaDTO(null, 1, 2, "S", "S", null);

        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));
        when(personaRepository.findById(2)).thenReturn(Optional.of(persona));
        when(cuentaAsociadaRepository.findByCuenta_IdCuen(1)).thenReturn(List.of()); // 0 asociados activos

        CuentaAsociada guardado = CuentaAsociada.builder().idCuas(1).cuenta(cuenta).persona(persona)
                .activo("S").notifica("S").build();
        when(cuentaAsociadaRepository.save(any(CuentaAsociada.class))).thenReturn(guardado);

        CuentaAsociadaDTO resultado = service.create(dto);

        assertThat(resultado.idCuas()).isEqualTo(1);
        assertThat(resultado.idCuen()).isEqualTo(1);
        assertThat(resultado.idPers()).isEqualTo(2);
    }

    @Test
    void create_cuandoSeAlcanzoElMaximoDePantallas_deberiaLanzarBusinessException() {
        CuentaAsociadaDTO dto = new CuentaAsociadaDTO(null, 1, 2, "S", "S", null);

        CuentaAsociada activo1 = CuentaAsociada.builder().idCuas(10).cuenta(cuenta)
                .persona(Persona.builder().idPers(3).build()).activo("S").notifica("S").build();
        CuentaAsociada activo2 = CuentaAsociada.builder().idCuas(11).cuenta(cuenta)
                .persona(Persona.builder().idPers(4).build()).activo("S").notifica("S").build();

        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));
        when(personaRepository.findById(2)).thenReturn(Optional.of(persona));
        // Ya hay 2 activos y la plataforma (HBO) solo permite 2 -> no debe permitir un tercero
        when(cuentaAsociadaRepository.findByCuenta_IdCuen(1)).thenReturn(List.of(activo1, activo2));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("maximo");

        verify(cuentaAsociadaRepository, never()).save(any());
    }

    @Test
    void create_cuandoCuentaAsociadaInactivaNoCuentaParaElLimite_deberiaCrear() {
        CuentaAsociadaDTO dto = new CuentaAsociadaDTO(null, 1, 2, "S", "S", null);

        CuentaAsociada activo1 = CuentaAsociada.builder().idCuas(10).cuenta(cuenta)
                .persona(Persona.builder().idPers(3).build()).activo("S").notifica("S").build();
        CuentaAsociada inactivo = CuentaAsociada.builder().idCuas(11).cuenta(cuenta)
                .persona(Persona.builder().idPers(4).build()).activo("N").notifica("S").build();

        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));
        when(personaRepository.findById(2)).thenReturn(Optional.of(persona));
        // Solo 1 activo + 1 inactivo -> hay cupo (limite es 2 activos)
        when(cuentaAsociadaRepository.findByCuenta_IdCuen(1)).thenReturn(List.of(activo1, inactivo));
        when(cuentaAsociadaRepository.save(any(CuentaAsociada.class))).thenAnswer(inv -> inv.getArgument(0));

        CuentaAsociadaDTO resultado = service.create(dto);

        assertThat(resultado.idPers()).isEqualTo(2);
        verify(cuentaAsociadaRepository).save(any(CuentaAsociada.class));
    }

    @Test
    void create_cuandoCuentaNoExiste_deberiaLanzarResourceNotFound() {
        CuentaAsociadaDTO dto = new CuentaAsociadaDTO(null, 99, 2, "S", "S", null);

        when(cuentaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(cuentaAsociadaRepository, never()).save(any());
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        CuentaAsociada entity = CuentaAsociada.builder().idCuas(1).cuenta(cuenta).persona(persona)
                .activo("S").notifica("S").build();
        when(cuentaAsociadaRepository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        verify(cuentaAsociadaRepository).delete(entity);
    }
}

package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.CuentaDTO;
import co.com.wallacesoft.paymenow.entity.Cuenta;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.Plataforma;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.CuentaRepository;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.PlataformaRepository;
import co.com.wallacesoft.paymenow.service.impl.CuentaServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private PlataformaRepository plataformaRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CuentaServiceImpl service;

    private Plataforma plataforma;
    private Persona propietario;
    private Cuenta cuenta;

    @BeforeEach
    void setUp() {
        plataforma = Plataforma.builder().idPlat(1).nombre("Netflix").cantidadCuentas(4).valor(45000).build();
        propietario = Persona.builder().idPers(1).primerNombre("Juan").primerApellido("Perez")
                .sexo("M").identificacion("123").email("juan@correo.com").build();
        cuenta = Cuenta.builder()
                .idCuen(1)
                .plataforma(plataforma)
                .propietario(propietario)
                .activo("S")
                .fechaPago(LocalDate.of(2026, 9, 15))
                .build();
    }

    @Test
    void create_conReferenciasValidas_deberiaCrearCuenta() {
        CuentaDTO dto = new CuentaDTO(null, 1, 1, "S", LocalDate.of(2026, 9, 15), null);

        when(plataformaRepository.findById(1)).thenReturn(Optional.of(plataforma));
        when(personaRepository.findById(1)).thenReturn(Optional.of(propietario));
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(cuenta);

        CuentaDTO resultado = service.create(dto);

        assertThat(resultado.idCuen()).isEqualTo(1);
        assertThat(resultado.idPlat()).isEqualTo(1);
        assertThat(resultado.idPers()).isEqualTo(1);
    }

    @Test
    void create_cuandoPlataformaNoExiste_deberiaLanzarExcepcion() {
        CuentaDTO dto = new CuentaDTO(null, 99, 1, "S", LocalDate.of(2026, 9, 15), null);

        when(plataformaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void create_cuandoPropietarioNoExiste_deberiaLanzarExcepcion() {
        CuentaDTO dto = new CuentaDTO(null, 1, 99, "S", LocalDate.of(2026, 9, 15), null);

        when(plataformaRepository.findById(1)).thenReturn(Optional.of(plataforma));
        when(personaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void create_sinActivoEnDto_deberiaUsarSPorDefecto() {
        CuentaDTO dto = new CuentaDTO(null, 1, 1, null, LocalDate.of(2026, 9, 15), null);

        when(plataformaRepository.findById(1)).thenReturn(Optional.of(plataforma));
        when(personaRepository.findById(1)).thenReturn(Optional.of(propietario));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        CuentaDTO resultado = service.create(dto);

        assertThat(resultado.activo()).isEqualTo("S");
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(cuentaRepository.findById(1)).thenReturn(Optional.of(cuenta));

        service.delete(1);

        verify(cuentaRepository).delete(cuenta);
    }

    @Test
    void count_deberiaRetornarCantidadDeRegistros() {
        when(cuentaRepository.count()).thenReturn(3L);

        ContadorDTO resultado = service.count();

        assertThat(resultado.cantidad()).isEqualTo(3);
    }
}

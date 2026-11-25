package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.AsociarPersonaDTO;
import co.com.wallacesoft.paymenow.dto.CuentaAsociadaDTO;
import co.com.wallacesoft.paymenow.dto.CuentaDTO;
import co.com.wallacesoft.paymenow.dto.PersonaBasicaDTO;
import co.com.wallacesoft.paymenow.dto.PersonaDTO;
import co.com.wallacesoft.paymenow.entity.Cuenta;
import co.com.wallacesoft.paymenow.entity.CuentaAsociada;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.Plataforma;
import co.com.wallacesoft.paymenow.entity.TiposIdentificacion;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.CuentaAsociadaRepository;
import co.com.wallacesoft.paymenow.repository.CuentaRepository;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.PlataformaRepository;
import co.com.wallacesoft.paymenow.repository.TiposIdentificacionRepository;
import co.com.wallacesoft.paymenow.security.UsuarioActual;
import co.com.wallacesoft.paymenow.service.impl.CuentaAsociadaServiceImpl;
import co.com.wallacesoft.paymenow.service.impl.CuentaServiceImpl;
import co.com.wallacesoft.paymenow.service.impl.PersonaServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Un cliente (persona 1) solo puede alcanzar sus datos; la persona 2 es otro cliente. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccesoClienteServiceTest {

    private static final LocalDate FECHA = LocalDate.of(2030, 1, 15);

    @Mock private PersonaRepository personaRepository;
    @Mock private TiposIdentificacionRepository tiposIdentificacionRepository;
    @Mock private PlataformaRepository plataformaRepository;
    @Mock private CuentaRepository cuentaRepository;
    @Mock private CuentaAsociadaRepository cuentaAsociadaRepository;
    @Mock private EntityManager entityManager;
    @Mock private UsuarioActual usuarioActual;

    private PersonaServiceImpl personaService;
    private CuentaServiceImpl cuentaService;
    private CuentaAsociadaServiceImpl asociadaService;

    private Persona yo;
    private Persona otro;
    private Plataforma plataforma;
    private Cuenta miCuenta;
    private Cuenta cuentaAjena;

    @BeforeEach
    void setUp() {
        personaService = new PersonaServiceImpl(personaRepository, tiposIdentificacionRepository, entityManager, usuarioActual);
        cuentaService = new CuentaServiceImpl(cuentaRepository, plataformaRepository, personaRepository,
                cuentaAsociadaRepository, entityManager, usuarioActual);
        asociadaService = new CuentaAsociadaServiceImpl(cuentaAsociadaRepository, cuentaRepository, personaRepository,
                entityManager, usuarioActual);

        TiposIdentificacion tipo = TiposIdentificacion.builder().idTiid(1).abreviatura("CC").build();
        yo = Persona.builder().idPers(1).primerNombre("Ana").primerApellido("Gomez")
                .identificacion("111").activo("S").tipoIdentificacion(tipo).build();
        otro = Persona.builder().idPers(2).primerNombre("Luis").primerApellido("Perez")
                .identificacion("222").activo("S").tipoIdentificacion(tipo).build();
        plataforma = Plataforma.builder().idPlat(1).nombre("HBO").cantidadCuentas(4).valor(1000).build();
        miCuenta = Cuenta.builder().idCuen(1).plataforma(plataforma).propietario(yo).activo("S").fechaPago(FECHA).build();
        cuentaAjena = Cuenta.builder().idCuen(2).plataforma(plataforma).propietario(otro).activo("S").fechaPago(FECHA).build();

        when(usuarioActual.esAdmin()).thenReturn(false);
        when(usuarioActual.idPersona()).thenReturn(1);
        when(personaRepository.findById(1)).thenReturn(Optional.of(yo));
        when(personaRepository.findById(2)).thenReturn(Optional.of(otro));
        when(plataformaRepository.findById(1)).thenReturn(Optional.of(plataforma));
        when(cuentaRepository.findById(1)).thenReturn(Optional.of(miCuenta));
        when(cuentaRepository.findById(2)).thenReturn(Optional.of(cuentaAjena));
        when(cuentaRepository.save(any(Cuenta.class))).thenAnswer(i -> i.getArgument(0));
        when(cuentaAsociadaRepository.save(any(CuentaAsociada.class))).thenAnswer(i -> i.getArgument(0));
    }

    private CuentaDTO cuentaDTO(Integer idPers) {
        return new CuentaDTO(null, 1, idPers, "S", FECHA, null, null, null);
    }

    private AsociarPersonaDTO asociarDTO(Integer idCuen, String identificacion) {
        return new AsociarPersonaDTO(idCuen, identificacion, "S", "S");
    }

    private CuentaAsociadaDTO asociadaDTO(Integer idCuen, Integer idPers) {
        return new CuentaAsociadaDTO(null, idCuen, idPers, "S", "S", null, null, null);
    }

    @Test
    void personas_soloDeberiaVerLaPropia() {
        assertThat(personaService.findAll()).extracting(PersonaDTO::idPers).containsExactly(1);
        assertThat(personaService.findAll(PageRequest.of(0, 10)).getContent())
                .extracting(PersonaDTO::idPers).containsExactly(1);
        assertThat(personaService.findAll(PageRequest.of(1, 10)).getContent()).isEmpty();
        assertThat(personaService.count().cantidad()).isEqualTo(1);
        assertThat(personaService.findById(1).idPers()).isEqualTo(1);

        when(personaRepository.findByIdentificacion("222")).thenReturn(Optional.of(otro));
        assertThatThrownBy(() -> personaService.findById(2)).isInstanceOf(ResourceNotFoundException.class);
        assertThatThrownBy(() -> personaService.findByIdentificacion("222")).isInstanceOf(ResourceNotFoundException.class);
        verify(personaRepository, never()).findAll();
    }

    @Test
    void busquedaBasica_deberiaDevolverSoloIdYNombreDePersonasActivas() {
        when(personaRepository.findByIdentificacion("222")).thenReturn(Optional.of(otro));
        assertThat(personaService.buscarBasicaPorIdentificacion("222"))
                .isEqualTo(new PersonaBasicaDTO("Luis Perez"));

        otro.setActivo("N");
        assertThatThrownBy(() -> personaService.buscarBasicaPorIdentificacion("222"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void cuentas_listadoYConteoDeberianUsarSoloLasVisibles() {
        when(cuentaRepository.findVisiblesPara(1)).thenReturn(List.of(miCuenta));

        assertThat(cuentaService.findAll()).extracting(CuentaDTO::idCuen).containsExactly(1);
        assertThat(cuentaService.count().cantidad()).isEqualTo(1);
        verify(cuentaRepository, never()).findAll();
        verify(cuentaRepository, never()).count();
    }

    @Test
    void cuentaAjena_soloDeberiaVerseSiEstaAsociadoDeFormaActiva() {
        assertThatThrownBy(() -> cuentaService.findById(2)).isInstanceOf(ResourceNotFoundException.class);

        when(cuentaAsociadaRepository.existsByCuenta_IdCuenAndPersona_IdPersAndActivo(2, 1, "S")).thenReturn(true);
        assertThat(cuentaService.findById(2).idCuen()).isEqualTo(2);
    }

    @Test
    void crearCuenta_deberiaIgnorarElTitularEnviado() {
        cuentaService.create(cuentaDTO(2));

        ArgumentCaptor<Cuenta> guardada = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepository).save(guardada.capture());
        assertThat(guardada.getValue().getPropietario()).isSameAs(yo);
    }

    @Test
    void modificarCuenta_noDeberiaCambiarTitularNiTocarCuentasAjenas() {
        cuentaService.update(1, cuentaDTO(2));
        assertThat(miCuenta.getPropietario()).isSameAs(yo);

        // Estar asociado permite ver una cuenta ajena, pero no modificarla
        when(cuentaAsociadaRepository.existsByCuenta_IdCuenAndPersona_IdPersAndActivo(2, 1, "S")).thenReturn(true);
        assertThatThrownBy(() -> cuentaService.update(2, cuentaDTO(2))).isInstanceOf(ResourceNotFoundException.class);
        verify(cuentaRepository, never()).save(cuentaAjena);
    }

    @Test
    void asociadas_deberiaGestionarSoloLasDeSusCuentas() {
        when(cuentaAsociadaRepository.findByCuenta_IdCuen(1)).thenReturn(List.of());
        CuentaAsociada deMiCuenta = CuentaAsociada.builder().idCuas(1).cuenta(miCuenta).persona(otro).activo("S").notifica("S").build();
        when(cuentaAsociadaRepository.save(any(CuentaAsociada.class))).thenReturn(deMiCuenta);

        when(personaRepository.findByIdentificacion("222")).thenReturn(Optional.of(otro));

        assertThat(asociadaService.createPorIdentificacion(asociarDTO(1, " 222 ")).idCuas()).isEqualTo(1);
        assertThatThrownBy(() -> asociadaService.createPorIdentificacion(asociarDTO(2, "222")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void asociarPorIdentificacion_noDeberiaAceptarPersonasInactivas() {
        otro.setActivo("N");
        when(personaRepository.findByIdentificacion("222")).thenReturn(Optional.of(otro));

        assertThatThrownBy(() -> asociadaService.createPorIdentificacion(asociarDTO(1, "222")))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(cuentaAsociadaRepository, never()).save(any());
    }

    @Test
    void asociacionPropiaEnCuentaAjena_deberiaSerSoloLectura() {
        CuentaAsociada mia = CuentaAsociada.builder().idCuas(5).cuenta(cuentaAjena).persona(yo).activo("S").notifica("S").build();
        when(cuentaAsociadaRepository.findById(5)).thenReturn(Optional.of(mia));

        assertThat(asociadaService.findById(5).idCuas()).isEqualTo(5);
        assertThatThrownBy(() -> asociadaService.update(5, asociadaDTO(2, 1))).isInstanceOf(ResourceNotFoundException.class);

        mia.setActivo("N");
        assertThatThrownBy(() -> asociadaService.findById(5)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void modificarAsociacion_soloDeberiaCambiarEstadoYNotificaciones() {
        CuentaAsociada deMiCuenta = CuentaAsociada.builder().idCuas(6).cuenta(miCuenta).persona(otro).activo("S").notifica("S").build();
        when(cuentaAsociadaRepository.findById(6)).thenReturn(Optional.of(deMiCuenta));

        // El id de otra persona no debe usarse para descubrir su nombre
        CuentaAsociadaDTO respuesta = asociadaService.update(6,
                new CuentaAsociadaDTO(null, 2, 99, "N", "N", null, null, null));

        assertThat(respuesta.nombrePersona()).isEqualTo("Luis Perez");
        assertThat(deMiCuenta.getCuenta()).isSameAs(miCuenta);
        assertThat(deMiCuenta.getPersona()).isSameAs(otro);
        assertThat(deMiCuenta.getActivo()).isEqualTo("N");
        assertThat(deMiCuenta.getNotifica()).isEqualTo("N");
        verify(personaRepository, never()).findById(99);
    }
}

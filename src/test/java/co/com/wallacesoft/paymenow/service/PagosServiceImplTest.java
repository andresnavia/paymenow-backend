package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PagosDTO;
import co.com.wallacesoft.paymenow.entity.CuentaAsociada;
import co.com.wallacesoft.paymenow.entity.EstadosPago;
import co.com.wallacesoft.paymenow.entity.Pagos;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.CuentaAsociadaRepository;
import co.com.wallacesoft.paymenow.repository.EstadosPagoRepository;
import co.com.wallacesoft.paymenow.repository.PagosRepository;
import co.com.wallacesoft.paymenow.service.impl.PagosServiceImpl;
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
class PagosServiceImplTest {

    @Mock
    private PagosRepository pagosRepository;

    @Mock
    private CuentaAsociadaRepository cuentaAsociadaRepository;

    @Mock
    private EstadosPagoRepository estadosPagoRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PagosServiceImpl service;

    private CuentaAsociada cuentaAsociada;
    private EstadosPago estadoPago;
    private Pagos pago;

    @BeforeEach
    void setUp() {
        cuentaAsociada = CuentaAsociada.builder().idCuas(1).activo("S").notifica("S").build();
        estadoPago = EstadosPago.builder().idEspa(1).nombre("PAGADO").build();
        pago = Pagos.builder()
                .idPago(1)
                .fechaPago(LocalDate.of(2026, 9, 1))
                .cuentaAsociada(cuentaAsociada)
                .estadoPago(estadoPago)
                .build();
    }

    @Test
    void create_conReferenciasValidas_deberiaCrearPago() {
        PagosDTO dto = new PagosDTO(null, LocalDate.of(2026, 9, 1), 1, 1, null);

        when(cuentaAsociadaRepository.findById(1)).thenReturn(Optional.of(cuentaAsociada));
        when(estadosPagoRepository.findById(1)).thenReturn(Optional.of(estadoPago));
        when(pagosRepository.save(any(Pagos.class))).thenReturn(pago);

        PagosDTO resultado = service.create(dto);

        assertThat(resultado.idPago()).isEqualTo(1);
        assertThat(resultado.idCuas()).isEqualTo(1);
        assertThat(resultado.idEspa()).isEqualTo(1);
    }

    @Test
    void create_cuandoCuentaAsociadaNoExiste_deberiaLanzarExcepcion() {
        PagosDTO dto = new PagosDTO(null, LocalDate.of(2026, 9, 1), 99, 1, null);

        when(cuentaAsociadaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(pagosRepository, never()).save(any());
    }

    @Test
    void create_cuandoEstadoPagoNoExiste_deberiaLanzarExcepcion() {
        PagosDTO dto = new PagosDTO(null, LocalDate.of(2026, 9, 1), 1, 99, null);

        when(cuentaAsociadaRepository.findById(1)).thenReturn(Optional.of(cuentaAsociada));
        when(estadosPagoRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(pagosRepository, never()).save(any());
    }

    @Test
    void update_deberiaActualizarFechaYEstado() {
        EstadosPago nuevoEstado = EstadosPago.builder().idEspa(2).nombre("VENCIDO").build();
        PagosDTO dto = new PagosDTO(1, LocalDate.of(2026, 9, 10), 1, 2, null);

        when(pagosRepository.findById(1)).thenReturn(Optional.of(pago));
        when(cuentaAsociadaRepository.findById(1)).thenReturn(Optional.of(cuentaAsociada));
        when(estadosPagoRepository.findById(2)).thenReturn(Optional.of(nuevoEstado));
        when(pagosRepository.save(any(Pagos.class))).thenAnswer(inv -> inv.getArgument(0));

        PagosDTO resultado = service.update(1, dto);

        assertThat(resultado.idEspa()).isEqualTo(2);
        assertThat(resultado.fechaPago()).isEqualTo(LocalDate.of(2026, 9, 10));
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(pagosRepository.findById(1)).thenReturn(Optional.of(pago));

        service.delete(1);

        verify(pagosRepository).delete(pago);
    }

    @Test
    void count_deberiaRetornarCantidadDeRegistros() {
        when(pagosRepository.count()).thenReturn(10L);

        ContadorDTO resultado = service.count();

        assertThat(resultado.cantidad()).isEqualTo(10);
    }
}

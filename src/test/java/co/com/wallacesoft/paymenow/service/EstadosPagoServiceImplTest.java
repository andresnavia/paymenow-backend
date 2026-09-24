package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.EstadosPagoDTO;
import co.com.wallacesoft.paymenow.entity.EstadosPago;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.EstadosPagoRepository;
import co.com.wallacesoft.paymenow.service.impl.EstadosPagoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstadosPagoServiceImplTest {

    @Mock
    private EstadosPagoRepository repository;

    @InjectMocks
    private EstadosPagoServiceImpl service;

    private EstadosPago entity;

    @BeforeEach
    void setUp() {
        entity = EstadosPago.builder().idEspa(1).nombre("PAGADO").descripcion("Pago realizado").build();
    }

    @Test
    void findAll_deberiaRetornarListaDeDTOs() {
        when(repository.findAll()).thenReturn(List.of(entity));

        List<EstadosPagoDTO> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("PAGADO");
    }

    @Test
    void findById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_deberiaGuardarYRetornarDTO() {
        EstadosPagoDTO dto = new EstadosPagoDTO(null, "PENDIENTE", "Pago aun no realizado");
        EstadosPago guardado = EstadosPago.builder().idEspa(2).nombre("PENDIENTE").descripcion("Pago aun no realizado").build();

        when(repository.save(any(EstadosPago.class))).thenReturn(guardado);

        EstadosPagoDTO resultado = service.create(dto);

        assertThat(resultado.idEspa()).isEqualTo(2);
        assertThat(resultado.nombre()).isEqualTo("PENDIENTE");
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        verify(repository).delete(entity);
    }
}

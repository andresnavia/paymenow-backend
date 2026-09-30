package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.TiposIdentificacionDTO;
import co.com.wallacesoft.paymenow.entity.TiposIdentificacion;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.TiposIdentificacionRepository;
import co.com.wallacesoft.paymenow.service.impl.TiposIdentificacionServiceImpl;
import jakarta.persistence.EntityManager;
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
class TiposIdentificacionServiceImplTest {

    @Mock
    private TiposIdentificacionRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TiposIdentificacionServiceImpl service;

    private TiposIdentificacion entity;

    @BeforeEach
    void setUp() {
        entity = TiposIdentificacion.builder()
                .idTiid(1)
                .abreviatura("CC")
                .descripcion("Cedula de ciudadania")
                .build();
    }

    @Test
    void findAll_deberiaRetornarListaDeDTOs() {
        when(repository.findAll()).thenReturn(List.of(entity));

        List<TiposIdentificacionDTO> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).abreviatura()).isEqualTo("CC");
        verify(repository).findAll();
    }

    @Test
    void findById_cuandoExiste_deberiaRetornarDTO() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        TiposIdentificacionDTO resultado = service.findById(1);

        assertThat(resultado.idTiid()).isEqualTo(1);
        assertThat(resultado.descripcion()).isEqualTo("Cedula de ciudadania");
    }

    @Test
    void findById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_deberiaGuardarYRetornarDTO() {
        TiposIdentificacionDTO dto = new TiposIdentificacionDTO(null, "CE", "Cedula de extranjeria", null, null);
        TiposIdentificacion guardado = TiposIdentificacion.builder()
                .idTiid(2).abreviatura("CE").descripcion("Cedula de extranjeria").build();

        when(repository.save(any(TiposIdentificacion.class))).thenReturn(guardado);

        TiposIdentificacionDTO resultado = service.create(dto);

        assertThat(resultado.idTiid()).isEqualTo(2);
        assertThat(resultado.abreviatura()).isEqualTo("CE");
        verify(repository).save(any(TiposIdentificacion.class));
    }

    @Test
    void update_cuandoExiste_deberiaActualizarCampos() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any(TiposIdentificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        TiposIdentificacionDTO dto = new TiposIdentificacionDTO(1, "TI", "Tarjeta de identidad", null, null);
        TiposIdentificacionDTO resultado = service.update(1, dto);

        assertThat(resultado.abreviatura()).isEqualTo("TI");
        assertThat(resultado.descripcion()).isEqualTo("Tarjeta de identidad");
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        verify(repository).delete(entity);
    }

    @Test
    void delete_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any());
    }
}

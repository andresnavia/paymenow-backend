package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.PlataformaDTO;
import co.com.wallacesoft.paymenow.entity.Plataforma;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.PlataformaRepository;
import co.com.wallacesoft.paymenow.service.impl.PlataformaServiceImpl;
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
class PlataformaServiceImplTest {

    @Mock
    private PlataformaRepository repository;

    @InjectMocks
    private PlataformaServiceImpl service;

    private Plataforma entity;

    @BeforeEach
    void setUp() {
        entity = Plataforma.builder()
                .idPlat(1)
                .nombre("Netflix")
                .descripcion("Plataforma de streaming de peliculas y series")
                .cantidadCuentas(4)
                .valor(45000)
                .build();
    }

    @Test
    void findAll_deberiaRetornarListaDeDTOs() {
        when(repository.findAll()).thenReturn(List.of(entity));

        List<PlataformaDTO> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Netflix");
    }

    @Test
    void findById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(50)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(50))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_deberiaGuardarYRetornarDTO() {
        PlataformaDTO dto = new PlataformaDTO(null, "Disney+", "Streaming Disney", 4, 30000);
        Plataforma guardado = Plataforma.builder()
                .idPlat(2).nombre("Disney+").descripcion("Streaming Disney")
                .cantidadCuentas(4).valor(30000).build();

        when(repository.save(any(Plataforma.class))).thenReturn(guardado);

        PlataformaDTO resultado = service.create(dto);

        assertThat(resultado.idPlat()).isEqualTo(2);
        assertThat(resultado.nombre()).isEqualTo("Disney+");
        assertThat(resultado.cantidadCuentas()).isEqualTo(4);
    }

    @Test
    void update_cuandoExiste_deberiaActualizarValores() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any(Plataforma.class))).thenAnswer(inv -> inv.getArgument(0));

        PlataformaDTO dto = new PlataformaDTO(1, "Netflix Premium", "Plan 4K", 4, 55000);
        PlataformaDTO resultado = service.update(1, dto);

        assertThat(resultado.nombre()).isEqualTo("Netflix Premium");
        assertThat(resultado.valor()).isEqualTo(55000);
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        verify(repository).delete(entity);
    }
}

package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.ParametrosDTO;
import co.com.wallacesoft.paymenow.entity.Parametros;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.ParametrosRepository;
import co.com.wallacesoft.paymenow.service.impl.ParametrosServiceImpl;
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
class ParametrosServiceImplTest {

    @Mock
    private ParametrosRepository repository;

    @InjectMocks
    private ParametrosServiceImpl service;

    private Parametros entity;

    @BeforeEach
    void setUp() {
        entity = Parametros.builder().idPara(1).nombre("DIAS_GRACIA").descripcion("Dias de gracia antes de suspender").valor("3").build();
    }

    @Test
    void findAll_deberiaRetornarListaDeDTOs() {
        when(repository.findAll()).thenReturn(List.of(entity));

        List<ParametrosDTO> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).valor()).isEqualTo("3");
    }

    @Test
    void findById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_cuandoExiste_deberiaActualizarValores() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any(Parametros.class))).thenAnswer(inv -> inv.getArgument(0));

        ParametrosDTO dto = new ParametrosDTO(1, "DIAS_GRACIA", "Actualizado", "5");
        ParametrosDTO resultado = service.update(1, dto);

        assertThat(resultado.valor()).isEqualTo("5");
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));

        service.delete(1);

        verify(repository).delete(entity);
    }
}

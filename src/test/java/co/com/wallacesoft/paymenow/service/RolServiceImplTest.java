package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.RolDTO;
import co.com.wallacesoft.paymenow.entity.Rol;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.RolRepository;
import co.com.wallacesoft.paymenow.service.impl.RolServiceImpl;
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
class RolServiceImplTest {

    @Mock
    private RolRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private RolServiceImpl service;

    private Rol entity;

    @BeforeEach
    void setUp() {
        entity = Rol.builder()
                .idRol(1)
                .nombre("ADMIN")
                .descripcion("Administrador del sistema")
                .activo("S")
                .build();
    }

    @Test
    void findAll_deberiaRetornarListaDeDTOs() {
        when(repository.findAll()).thenReturn(List.of(entity));

        List<RolDTO> resultado = service.findAll();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("ADMIN");
    }

    @Test
    void findById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_deberiaGuardarYRetornarDTO() {
        RolDTO dto = new RolDTO(null, "CLIENTE", "Usuario final de la aplicacion", "S", null);
        Rol guardado = Rol.builder().idRol(2).nombre("CLIENTE").descripcion("Usuario final de la aplicacion")
                .activo("S").build();

        when(repository.save(any(Rol.class))).thenReturn(guardado);

        RolDTO resultado = service.create(dto);

        assertThat(resultado.idRol()).isEqualTo(2);
        assertThat(resultado.nombre()).isEqualTo("CLIENTE");
    }

    @Test
    void create_sinActivoEnDto_deberiaUsarSPorDefecto() {
        RolDTO dto = new RolDTO(null, "CLIENTE", "Usuario final de la aplicacion", null, null);

        when(repository.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        RolDTO resultado = service.create(dto);

        assertThat(resultado.activo()).isEqualTo("S");
    }

    @Test
    void update_cuandoExiste_deberiaActualizarValores() {
        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        RolDTO dto = new RolDTO(1, "SUPERADMIN", "Administrador con todos los permisos", "N", null);
        RolDTO resultado = service.update(1, dto);

        assertThat(resultado.nombre()).isEqualTo("SUPERADMIN");
        assertThat(resultado.activo()).isEqualTo("N");
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

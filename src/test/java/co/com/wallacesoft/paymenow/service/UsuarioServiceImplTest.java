package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.UsuarioDTO;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.Rol;
import co.com.wallacesoft.paymenow.entity.Usuario;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.RolRepository;
import co.com.wallacesoft.paymenow.repository.UsuarioRepository;
import co.com.wallacesoft.paymenow.service.impl.UsuarioServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UsuarioServiceImpl service;

    private Persona persona;
    private Rol rol;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        persona = Persona.builder().idPers(1).primerNombre("Ana").primerApellido("Gomez")
                .sexo("F").identificacion("456").email("ana@correo.com").build();
        rol = Rol.builder().idRol(1).nombre("CLIENTE").activo("S").build();
        usuario = Usuario.builder()
                .idUsua(1)
                .persona(persona)
                .rol(rol)
                .activo("S")
                .idFirebase("firebase-token-1")
                .build();
    }

    @Test
    void findById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_conReferenciasValidas_deberiaCrearUsuario() {
        UsuarioDTO dto = new UsuarioDTO(null, 1, 1, "firebase-token-2", "S", null);

        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(usuarioRepository.existsByPersona_IdPersAndRol_IdRol(1, 1)).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO resultado = service.create(dto);

        assertThat(resultado.idUsua()).isEqualTo(1);
        assertThat(resultado.idPers()).isEqualTo(1);
        assertThat(resultado.idRol()).isEqualTo(1);
        verify(entityManager).refresh(usuario);
    }

    @Test
    void create_cuandoPersonaNoExiste_deberiaLanzarResourceNotFound() {
        UsuarioDTO dto = new UsuarioDTO(null, 99, 1, "firebase-token-2", "S", null);

        when(personaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void create_cuandoRolNoExiste_deberiaLanzarResourceNotFound() {
        UsuarioDTO dto = new UsuarioDTO(null, 1, 99, "firebase-token-2", "S", null);

        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void create_cuandoYaExisteUsuarioConMismaPersonaYRol_deberiaLanzarBusinessException() {
        UsuarioDTO dto = new UsuarioDTO(null, 1, 1, "firebase-token-2", "S", null);

        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(usuarioRepository.existsByPersona_IdPersAndRol_IdRol(1, 1)).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void create_sinActivoEnDto_deberiaUsarSPorDefecto() {
        UsuarioDTO dto = new UsuarioDTO(null, 1, 1, "firebase-token-2", null, null);

        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(usuarioRepository.existsByPersona_IdPersAndRol_IdRol(1, 1)).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO resultado = service.create(dto);

        assertThat(resultado.activo()).isEqualTo("S");
    }

    @Test
    void update_cambiandoRolAUnaCombinacionYaExistente_deberiaLanzarBusinessException() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        UsuarioDTO dto = new UsuarioDTO(1, 1, 2, "firebase-token-1", "S", null);

        when(usuarioRepository.existsByPersona_IdPersAndRol_IdRol(1, 2)).thenReturn(true);

        assertThatThrownBy(() -> service.update(1, dto))
                .isInstanceOf(BusinessException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void update_manteniendoLaMismaPersonaYRol_deberiaActualizarSinValidarDuplicado() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(rolRepository.findById(1)).thenReturn(Optional.of(rol));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioDTO dto = new UsuarioDTO(1, 1, 1, "firebase-token-actualizado", "N", null);
        UsuarioDTO resultado = service.update(1, dto);

        assertThat(resultado.idFirebase()).isEqualTo("firebase-token-actualizado");
        assertThat(resultado.activo()).isEqualTo("N");
        verify(usuarioRepository, never()).existsByPersona_IdPersAndRol_IdRol(any(), any());
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        service.delete(1);

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void delete_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(usuarioRepository, never()).delete(any());
    }
}

package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.PersonaDTO;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.TiposIdentificacion;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.TiposIdentificacionRepository;
import co.com.wallacesoft.paymenow.service.impl.PersonaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceImplTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private TiposIdentificacionRepository tiposIdentificacionRepository;

    @InjectMocks
    private PersonaServiceImpl service;

    private TiposIdentificacion tipo;
    private Persona persona;
    private PersonaDTO dto;

    @BeforeEach
    void setUp() {
        tipo = TiposIdentificacion.builder().idTiid(1).abreviatura("CC").descripcion("Cedula").build();

        persona = Persona.builder()
                .idPers(1)
                .primerNombre("Juan")
                .primerApellido("Perez")
                .sexo("M")
                .fechaNacimiento(LocalDate.of(1990, 5, 10))
                .tipoIdentificacion(tipo)
                .identificacion("123456")
                .email("juan@correo.com")
                .build();

        dto = new PersonaDTO(null, "Juan", null, "Perez", null, "M",
                LocalDate.of(1990, 5, 10), 1, "CC", "123456", "juan@correo.com");
    }

    @Test
    void create_cuandoIdentificacionNoExiste_deberiaCrearPersona() {
        when(personaRepository.existsByIdentificacion("123456")).thenReturn(false);
        when(tiposIdentificacionRepository.findById(1)).thenReturn(Optional.of(tipo));
        when(personaRepository.save(any(Persona.class))).thenReturn(persona);

        PersonaDTO resultado = service.create(dto);

        assertThat(resultado.idPers()).isEqualTo(1);
        assertThat(resultado.identificacion()).isEqualTo("123456");
        assertThat(resultado.idTiid()).isEqualTo(1);
        verify(personaRepository).save(any(Persona.class));
    }

    @Test
    void create_cuandoIdentificacionYaExiste_deberiaLanzarBusinessException() {
        when(personaRepository.existsByIdentificacion("123456")).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("123456");

        verify(personaRepository, never()).save(any());
    }

    @Test
    void create_cuandoTipoIdentificacionNoExiste_deberiaLanzarResourceNotFound() {
        when(personaRepository.existsByIdentificacion("123456")).thenReturn(false);
        when(tiposIdentificacionRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(personaRepository, never()).save(any());
    }

    @Test
    void findAll_conPaginacion_deberiaRetornarPaginaDeDTOs() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Persona> paginaEntidades = new PageImpl<>(List.of(persona), pageable, 1);
        when(personaRepository.findAll(pageable)).thenReturn(paginaEntidades);

        Page<PersonaDTO> resultado = service.findAll(pageable);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).identificacion()).isEqualTo("123456");
    }

    @Test
    void findById_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(personaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findByIdentificacion_cuandoNoExiste_deberiaLanzarExcepcion() {
        when(personaRepository.findByIdentificacion("1161152361")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findByIdentificacion("1161152361"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_cambiandoIdentificacionAUnaYaExistente_deberiaLanzarBusinessException() {
        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(personaRepository.existsByIdentificacion("999")).thenReturn(true);

        PersonaDTO dtoConNuevaIdentificacion = new PersonaDTO(1, "Juan", null, "Perez", null, "M",
                LocalDate.of(1990, 5, 10), 1, "CC", "999", "juan@correo.com");

        assertThatThrownBy(() -> service.update(1, dtoConNuevaIdentificacion))
                .isInstanceOf(BusinessException.class);

        verify(personaRepository, never()).save(any());
    }

    @Test
    void update_manteniendoLaMismaIdentificacion_deberiaActualizarSinError() {
        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(tiposIdentificacionRepository.findById(1)).thenReturn(Optional.of(tipo));
        when(personaRepository.save(any(Persona.class))).thenAnswer(inv -> inv.getArgument(0));

        PersonaDTO dtoActualizado = new PersonaDTO(1, "Juan Carlos", null, "Perez", null, "M",
                LocalDate.of(1990, 5, 10), 1, "CC", "123456", "juan@correo.com");

        PersonaDTO resultado = service.update(1, dtoActualizado);

        assertThat(resultado.primerNombre()).isEqualTo("Juan Carlos");
        verify(personaRepository, never()).existsByIdentificacion(any());
    }

    @Test
    void delete_cuandoExiste_deberiaEliminar() {
        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));

        service.delete(1);

        verify(personaRepository).delete(persona);
    }
}

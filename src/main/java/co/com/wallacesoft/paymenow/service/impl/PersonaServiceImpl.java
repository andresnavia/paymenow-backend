package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.PersonaBasicaDTO;
import co.com.wallacesoft.paymenow.security.UsuarioActual;
import org.springframework.data.domain.PageImpl;
import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PersonaDTO;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.TiposIdentificacion;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.TiposIdentificacionRepository;
import co.com.wallacesoft.paymenow.service.PersonaService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final TiposIdentificacionRepository tiposIdentificacionRepository;
    private final EntityManager entityManager;
    private final UsuarioActual usuarioActual;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> findAll() {
        if (!usuarioActual.esAdmin()) {
            return List.of(toDTO(getEntityOrThrow(usuarioActual.idPersona())));
        }
        return personaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaDTO> findAll(Pageable pageable) {
        if (!usuarioActual.esAdmin()) {
            List<PersonaDTO> contenido = pageable.getOffset() == 0
                    ? List.of(toDTO(getEntityOrThrow(usuarioActual.idPersona())))
                    : List.of();
            return new PageImpl<>(contenido, pageable, 1);
        }
        return personaRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public PersonaDTO findByIdentificacion(String identificacion) {
        Persona entity = getEntityByIdentificacionOrThrow(identificacion);
        if (!puedeVer(entity)) {
            throw new ResourceNotFoundException("Persona", identificacion);
        }
        return toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersonaDTO> buscarPorIdentificacion(String identificacion) {
        return personaRepository.findByIdentificacion(identificacion).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaBasicaDTO buscarBasicaPorIdentificacion(String identificacion) {
        Persona entity = getEntityByIdentificacionOrThrow(identificacion);
        if (!"S".equalsIgnoreCase(entity.getActivo())) {
            throw new ResourceNotFoundException("Persona", identificacion);
        }
        return new PersonaBasicaDTO(entity.getNombreCompleto());
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findById(Integer id) {
        Persona entity = getEntityOrThrow(id);
        if (!puedeVer(entity)) {
            throw new ResourceNotFoundException("Persona", id);
        }
        return toDTO(entity);
    }

    @Override
    public ContadorDTO count() {
        return new ContadorDTO(usuarioActual.esAdmin() ? personaRepository.count() : 1);
    }

    @Override
    public PersonaDTO create(PersonaDTO dto) {
        if (personaRepository.existsByIdentificacion(dto.identificacion())) {
            throw new BusinessException(
                    "Ya existe una persona registrada con la identificacion: " + dto.identificacion());
        }

        TiposIdentificacion tipo = getTipoIdentificacionOrThrow(dto.idTiid());

        Persona entity = Persona.builder()
                .primerNombre(dto.primerNombre())
                .segundoNombre(dto.segundoNombre())
                .primerApellido(dto.primerApellido())
                .segundoApellido(dto.segundoApellido())
                .sexo(dto.sexo())
                .fechaNacimiento(dto.fechaNacimiento())
                .tipoIdentificacion(tipo)
                .identificacion(dto.identificacion())
                .email(dto.email())
                .telefono(dto.telefono())
                .celular(dto.celular())
                .activo(StringUtils.hasText(dto.activo()) ? dto.activo() : "S")
                .build();

        Persona guardado = personaRepository.save(entity);
        entityManager.refresh(guardado);
        return toDTO(guardado);
    }

    @Override
    public PersonaDTO update(Integer id, PersonaDTO dto) {
        Persona entity = getEntityOrThrow(id);

        if (!entity.getIdentificacion().equals(dto.identificacion())
                && personaRepository.existsByIdentificacion(dto.identificacion())) {
            throw new BusinessException(
                    "Ya existe una persona registrada con la identificacion: " + dto.identificacion());
        }

        TiposIdentificacion tipo = getTipoIdentificacionOrThrow(dto.idTiid());

        entity.setPrimerNombre(dto.primerNombre());
        entity.setSegundoNombre(dto.segundoNombre());
        entity.setPrimerApellido(dto.primerApellido());
        entity.setSegundoApellido(dto.segundoApellido());
        entity.setSexo(dto.sexo());
        entity.setFechaNacimiento(dto.fechaNacimiento());
        entity.setTipoIdentificacion(tipo);
        entity.setIdentificacion(dto.identificacion());
        entity.setEmail(dto.email());
        entity.setTelefono(dto.telefono());
        entity.setCelular(dto.celular());
        entity.setActivo(StringUtils.hasText(dto.activo()) ? dto.activo() : entity.getActivo());

        return toDTO(personaRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        personaRepository.delete(getEntityOrThrow(id));
    }

    private boolean puedeVer(Persona entity) {
        return usuarioActual.esAdmin() || entity.getIdPers().equals(usuarioActual.idPersona());
    }

    private Persona getEntityOrThrow(Integer id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", id));
    }

    private Persona getEntityByIdentificacionOrThrow(String identificacion) {
        return personaRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", identificacion));
    }

    private TiposIdentificacion getTipoIdentificacionOrThrow(Integer idTiid) {
        return tiposIdentificacionRepository.findById(idTiid)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de identificacion", idTiid));
    }

    private PersonaDTO toDTO(Persona entity) {
        return new PersonaDTO(
                entity.getIdPers(),
                entity.getPrimerNombre(),
                entity.getSegundoNombre(),
                entity.getPrimerApellido(),
                entity.getSegundoApellido(),
                entity.getSexo(),
                entity.getFechaNacimiento(),
                entity.getTipoIdentificacion().getIdTiid(),
                entity.getTipoIdentificacion().getAbreviatura(),
                entity.getIdentificacion(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getCelular(),
                entity.getActivo(),
                entity.getFechaCreacion());
    }

}

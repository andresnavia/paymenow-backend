package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PersonaDTO;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.TiposIdentificacion;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.TiposIdentificacionRepository;
import co.com.wallacesoft.paymenow.service.PersonaService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final TiposIdentificacionRepository tiposIdentificacionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaDTO> findAll() {
        return personaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonaDTO> findAll(Pageable pageable) {
        return personaRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    public PersonaDTO findByIdentificacion(String identificacion) {
        return toDTO(getEntityByIdentificacionOrThrow(identificacion));
    }

    @Override
    @Transactional(readOnly = true)
    public PersonaDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public ContadorDTO count() {
        return new ContadorDTO(personaRepository.count());
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
                .build();

        return toDTO(personaRepository.save(entity));
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

        return toDTO(personaRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        personaRepository.delete(getEntityOrThrow(id));
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
                entity.getEmail());
    }

}

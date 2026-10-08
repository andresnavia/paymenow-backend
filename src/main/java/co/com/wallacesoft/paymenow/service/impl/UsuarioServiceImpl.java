package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.UsuarioDTO;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.Rol;
import co.com.wallacesoft.paymenow.entity.Usuario;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.RolRepository;
import co.com.wallacesoft.paymenow.repository.UsuarioRepository;
import co.com.wallacesoft.paymenow.service.UsuarioService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final RolRepository rolRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public UsuarioDTO create(UsuarioDTO dto) {
        Persona persona = getPersonaOrThrow(dto.idPers());
        Rol rol = getRolOrThrow(dto.idRol());

        if (usuarioRepository.existsByPersona_IdPersAndRol_IdRol(dto.idPers(), dto.idRol())) {
            throw new BusinessException(
                    "Ya existe un usuario para la persona %d con el rol %d".formatted(dto.idPers(), dto.idRol()));
        }

        Usuario entity = Usuario.builder()
                .persona(persona)
                .rol(rol)
                .idFirebase(dto.idFirebase())
                .activo(StringUtils.hasText(dto.activo()) ? dto.activo() : "S")
                .build();

        Usuario guardado = usuarioRepository.save(entity);
        entityManager.refresh(guardado);
        return toDTO(guardado);
    }

    @Override
    public UsuarioDTO update(Integer id, UsuarioDTO dto) {
        Usuario entity = getEntityOrThrow(id);

        boolean cambioPersonaORol = !entity.getPersona().getIdPers().equals(dto.idPers())
                || !entity.getRol().getIdRol().equals(dto.idRol());

        if (cambioPersonaORol && usuarioRepository.existsByPersona_IdPersAndRol_IdRol(dto.idPers(), dto.idRol())) {
            throw new BusinessException(
                    "Ya existe un usuario para la persona %d con el rol %d".formatted(dto.idPers(), dto.idRol()));
        }

        entity.setPersona(getPersonaOrThrow(dto.idPers()));
        entity.setRol(getRolOrThrow(dto.idRol()));
        entity.setIdFirebase(dto.idFirebase());
        entity.setActivo(StringUtils.hasText(dto.activo()) ? dto.activo() : entity.getActivo());

        return toDTO(usuarioRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        usuarioRepository.delete(getEntityOrThrow(id));
    }

    private Usuario getEntityOrThrow(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private Persona getPersonaOrThrow(Integer id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", id));
    }

    private Rol getRolOrThrow(Integer id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
    }

    private UsuarioDTO toDTO(Usuario entity) {
        return new UsuarioDTO(
                entity.getIdUsua(),
                entity.getPersona().getIdPers(),
                entity.getRol().getIdRol(),
                entity.getIdFirebase(),
                entity.getActivo(),
                entity.getFechaCreacion());
    }

    @Override
    public boolean existeUsuarioPersona(Integer idPers, Integer idRol) {
        return usuarioRepository.existsByPersona_IdPersAndRol_IdRol(idPers, idRol);
    }
}

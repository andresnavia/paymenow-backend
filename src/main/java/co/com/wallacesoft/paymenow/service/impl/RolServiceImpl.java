package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.RolDTO;
import co.com.wallacesoft.paymenow.entity.Rol;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.RolRepository;
import co.com.wallacesoft.paymenow.service.RolService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RolServiceImpl implements RolService {

    private final RolRepository repository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<RolDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RolDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public RolDTO create(RolDTO dto) {
        Rol entity = Rol.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .activo(StringUtils.hasText(dto.activo()) ? dto.activo() : "S")
                .build();
        Rol guardado = repository.save(entity);
        entityManager.refresh(guardado);
        return toDTO(guardado);
    }

    @Override
    public RolDTO update(Integer id, RolDTO dto) {
        Rol entity = getEntityOrThrow(id);
        entity.setNombre(dto.nombre());
        entity.setDescripcion(dto.descripcion());
        entity.setActivo(StringUtils.hasText(dto.activo()) ? dto.activo() : entity.getActivo());
        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        repository.delete(getEntityOrThrow(id));
    }

    private Rol getEntityOrThrow(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
    }

    private RolDTO toDTO(Rol entity) {
        return new RolDTO(entity.getIdRol(), entity.getNombre(), entity.getDescripcion(),
                entity.getActivo(), entity.getFechaCreacion());
    }
}

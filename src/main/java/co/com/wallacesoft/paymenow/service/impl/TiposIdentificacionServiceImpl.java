package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.TiposIdentificacionDTO;
import co.com.wallacesoft.paymenow.entity.TiposIdentificacion;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.TiposIdentificacionRepository;
import co.com.wallacesoft.paymenow.service.TiposIdentificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TiposIdentificacionServiceImpl implements TiposIdentificacionService {

    private final TiposIdentificacionRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<TiposIdentificacionDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TiposIdentificacionDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public TiposIdentificacionDTO create(TiposIdentificacionDTO dto) {
        TiposIdentificacion entity = TiposIdentificacion.builder()
                .abreviatura(dto.abreviatura())
                .descripcion(dto.descripcion())
                .build();
        return toDTO(repository.save(entity));
    }

    @Override
    public TiposIdentificacionDTO update(Integer id, TiposIdentificacionDTO dto) {
        TiposIdentificacion entity = getEntityOrThrow(id);
        entity.setAbreviatura(dto.abreviatura());
        entity.setDescripcion(dto.descripcion());
        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        TiposIdentificacion entity = getEntityOrThrow(id);
        repository.delete(entity);
    }

    private TiposIdentificacion getEntityOrThrow(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de identificacion", id));
    }

    private TiposIdentificacionDTO toDTO(TiposIdentificacion entity) {
        return new TiposIdentificacionDTO(entity.getIdTiid(), entity.getAbreviatura(), entity.getDescripcion());
    }
}

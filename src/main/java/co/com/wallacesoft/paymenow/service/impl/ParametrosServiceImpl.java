package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.ParametrosDTO;
import co.com.wallacesoft.paymenow.entity.Parametros;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.ParametrosRepository;
import co.com.wallacesoft.paymenow.service.ParametrosService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParametrosServiceImpl implements ParametrosService {

    private final ParametrosRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<ParametrosDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ParametrosDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public ParametrosDTO create(ParametrosDTO dto) {
        Parametros entity = Parametros.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .valor(dto.valor())
                .build();
        return toDTO(repository.save(entity));
    }

    @Override
    public ParametrosDTO update(Integer id, ParametrosDTO dto) {
        Parametros entity = getEntityOrThrow(id);
        entity.setNombre(dto.nombre());
        entity.setDescripcion(dto.descripcion());
        entity.setValor(dto.valor());
        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        repository.delete(getEntityOrThrow(id));
    }

    private Parametros getEntityOrThrow(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parametro", id));
    }

    private ParametrosDTO toDTO(Parametros entity) {
        return new ParametrosDTO(entity.getIdPara(), entity.getNombre(), entity.getDescripcion(), entity.getValor());
    }
}

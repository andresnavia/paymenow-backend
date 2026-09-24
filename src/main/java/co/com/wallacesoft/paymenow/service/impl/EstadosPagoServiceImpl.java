package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.EstadosPagoDTO;
import co.com.wallacesoft.paymenow.entity.EstadosPago;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.EstadosPagoRepository;
import co.com.wallacesoft.paymenow.service.EstadosPagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EstadosPagoServiceImpl implements EstadosPagoService {

    private final EstadosPagoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<EstadosPagoDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EstadosPagoDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public EstadosPagoDTO create(EstadosPagoDTO dto) {
        EstadosPago entity = EstadosPago.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .build();
        return toDTO(repository.save(entity));
    }

    @Override
    public EstadosPagoDTO update(Integer id, EstadosPagoDTO dto) {
        EstadosPago entity = getEntityOrThrow(id);
        entity.setNombre(dto.nombre());
        entity.setDescripcion(dto.descripcion());
        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        repository.delete(getEntityOrThrow(id));
    }

    private EstadosPago getEntityOrThrow(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado de pago", id));
    }

    private EstadosPagoDTO toDTO(EstadosPago entity) {
        return new EstadosPagoDTO(entity.getIdEspa(), entity.getNombre(), entity.getDescripcion());
    }
}

package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PlataformaDTO;
import co.com.wallacesoft.paymenow.entity.Plataforma;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.PlataformaRepository;
import co.com.wallacesoft.paymenow.service.PlataformaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlataformaServiceImpl implements PlataformaService {

    private final PlataformaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<PlataformaDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public ContadorDTO count() {
        return new ContadorDTO(repository.count());
    }

    @Override
    @Transactional(readOnly = true)
    public PlataformaDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public PlataformaDTO create(PlataformaDTO dto) {
        Plataforma entity = Plataforma.builder()
                .nombre(dto.nombre())
                .descripcion(dto.descripcion())
                .cantidadCuentas(dto.cantidadCuentas())
                .valor(dto.valor())
                .build();
        return toDTO(repository.save(entity));
    }

    @Override
    public PlataformaDTO update(Integer id, PlataformaDTO dto) {
        Plataforma entity = getEntityOrThrow(id);
        entity.setNombre(dto.nombre());
        entity.setDescripcion(dto.descripcion());
        entity.setCantidadCuentas(dto.cantidadCuentas());
        entity.setValor(dto.valor());
        return toDTO(repository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        repository.delete(getEntityOrThrow(id));
    }

    private Plataforma getEntityOrThrow(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plataforma", id));
    }

    private PlataformaDTO toDTO(Plataforma entity) {
        return new PlataformaDTO(entity.getIdPlat(), entity.getNombre(), entity.getDescripcion(),
                entity.getCantidadCuentas(), entity.getValor());
    }
}

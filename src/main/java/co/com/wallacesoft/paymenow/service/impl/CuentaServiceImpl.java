package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.CuentaDTO;
import co.com.wallacesoft.paymenow.entity.Cuenta;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.entity.Plataforma;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.CuentaRepository;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.repository.PlataformaRepository;
import co.com.wallacesoft.paymenow.service.CuentaService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository cuentaRepository;
    private final PlataformaRepository plataformaRepository;
    private final PersonaRepository personaRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> findAll() {
        return cuentaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public ContadorDTO count() {
        return new ContadorDTO(cuentaRepository.count());
    }

    @Override
    public CuentaDTO create(CuentaDTO dto) {
        Plataforma plataforma = getPlataformaOrThrow(dto.idPlat());
        Persona propietario = getPersonaOrThrow(dto.idPers());

        Cuenta entity = Cuenta.builder()
                .plataforma(plataforma)
                .propietario(propietario)
                .activo(StringUtils.hasText(dto.activo()) ? dto.activo() : "S")
                .fechaPago(dto.fechaPago())
                .build();

        Cuenta guardado = cuentaRepository.save(entity);
        entityManager.refresh(guardado);
        return toDTO(guardado);
    }

    @Override
    public CuentaDTO update(Integer id, CuentaDTO dto) {
        Cuenta entity = getEntityOrThrow(id);
        entity.setPlataforma(getPlataformaOrThrow(dto.idPlat()));
        entity.setPropietario(getPersonaOrThrow(dto.idPers()));
        entity.setActivo(StringUtils.hasText(dto.activo()) ? dto.activo() : entity.getActivo());
        entity.setFechaPago(dto.fechaPago());
        return toDTO(cuentaRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        cuentaRepository.delete(getEntityOrThrow(id));
    }

    private Cuenta getEntityOrThrow(Integer id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", id));
    }

    private Plataforma getPlataformaOrThrow(Integer id) {
        return plataformaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plataforma", id));
    }

    private Persona getPersonaOrThrow(Integer id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", id));
    }

    private CuentaDTO toDTO(Cuenta entity) {
        return new CuentaDTO(
                entity.getIdCuen(),
                entity.getPlataforma().getIdPlat(),
                entity.getPropietario().getIdPers(),
                entity.getActivo(),
                entity.getFechaPago(),
                entity.getFechaCreacion());
    }
}

package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.CuentaAsociadaDTO;
import co.com.wallacesoft.paymenow.entity.Cuenta;
import co.com.wallacesoft.paymenow.entity.CuentaAsociada;
import co.com.wallacesoft.paymenow.entity.Persona;
import co.com.wallacesoft.paymenow.exception.BusinessException;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.CuentaAsociadaRepository;
import co.com.wallacesoft.paymenow.repository.CuentaRepository;
import co.com.wallacesoft.paymenow.repository.PersonaRepository;
import co.com.wallacesoft.paymenow.service.CuentaAsociadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaAsociadaServiceImpl implements CuentaAsociadaService {

    private final CuentaAsociadaRepository cuentaAsociadaRepository;
    private final CuentaRepository cuentaRepository;
    private final PersonaRepository personaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaAsociadaDTO> findAll() {
        return cuentaAsociadaRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaAsociadaDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public CuentaAsociadaDTO create(CuentaAsociadaDTO dto) {
        Cuenta cuenta = getCuentaOrThrow(dto.idCuen());
        Persona persona = getPersonaOrThrow(dto.idPers());

        long asociadosActivos = cuentaAsociadaRepository.findByCuenta_IdCuen(cuenta.getIdCuen()).stream()
                .filter(ca -> "S".equalsIgnoreCase(ca.getActivo()))
                .count();

        if (asociadosActivos >= cuenta.getPlataforma().getCantidadCuentas()) {
            throw new BusinessException(
                    "La cuenta ya alcanzo el maximo de perfiles/pantallas permitidos por la plataforma (%d)."
                            .formatted(cuenta.getPlataforma().getCantidadCuentas()));
        }

        CuentaAsociada entity = CuentaAsociada.builder()
                .cuenta(cuenta)
                .persona(persona)
                .activo(StringUtils.hasText(dto.activo()) ? dto.activo() : "S")
                .notifica(StringUtils.hasText(dto.notifica()) ? dto.notifica() : "S")
                .build();

        return toDTO(cuentaAsociadaRepository.save(entity));
    }

    @Override
    public CuentaAsociadaDTO update(Integer id, CuentaAsociadaDTO dto) {
        CuentaAsociada entity = getEntityOrThrow(id);
        entity.setCuenta(getCuentaOrThrow(dto.idCuen()));
        entity.setPersona(getPersonaOrThrow(dto.idPers()));
        entity.setActivo(StringUtils.hasText(dto.activo()) ? dto.activo() : entity.getActivo());
        entity.setNotifica(StringUtils.hasText(dto.notifica()) ? dto.notifica() : entity.getNotifica());
        return toDTO(cuentaAsociadaRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        cuentaAsociadaRepository.delete(getEntityOrThrow(id));
    }

    private CuentaAsociada getEntityOrThrow(Integer id) {
        return cuentaAsociadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta asociada", id));
    }

    private Cuenta getCuentaOrThrow(Integer id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta", id));
    }

    private Persona getPersonaOrThrow(Integer id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Persona", id));
    }

    private CuentaAsociadaDTO toDTO(CuentaAsociada entity) {
        return new CuentaAsociadaDTO(
                entity.getIdCuas(),
                entity.getCuenta().getIdCuen(),
                entity.getPersona().getIdPers(),
                entity.getActivo(),
                entity.getNotifica()
        );
    }
}

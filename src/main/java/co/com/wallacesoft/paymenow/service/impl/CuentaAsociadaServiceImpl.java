package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.AsociarPersonaDTO;
import co.com.wallacesoft.paymenow.security.UsuarioActual;
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
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CuentaAsociadaServiceImpl implements CuentaAsociadaService {

    private static final String MSG_YA_ASOCIADA = "La Persona ya se encuentra asociada a esta cuenta.";

    private final CuentaAsociadaRepository cuentaAsociadaRepository;
    private final CuentaRepository cuentaRepository;
    private final PersonaRepository personaRepository;
    private final EntityManager entityManager;
    private final UsuarioActual usuarioActual;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaAsociadaDTO> findAll() {
        List<CuentaAsociada> asociadas = usuarioActual.esAdmin()
                ? cuentaAsociadaRepository.findAll()
                : cuentaAsociadaRepository.findVisiblesPara(usuarioActual.idPersona());
        return asociadas.stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaAsociadaDTO findById(Integer id) {
        CuentaAsociada entity = getEntityOrThrow(id);
        if (!puedeVer(entity)) {
            throw new ResourceNotFoundException("Cuenta asociada", id);
        }
        return toDTO(entity);
    }

    @Override
    public CuentaAsociadaDTO create(CuentaAsociadaDTO dto) {
        Cuenta cuenta = getCuentaModificableOrThrow(dto.idCuen());
        Persona persona = getPersonaOrThrow(dto.idPers());
        return crear(cuenta, persona, dto.activo(), dto.notifica());
    }

    @Override
    public CuentaAsociadaDTO createPorIdentificacion(AsociarPersonaDTO dto) {
        // La cuenta se valida primero para no revelar nada a quien no es su titular
        Cuenta cuenta = getCuentaModificableOrThrow(dto.idCuen());
        String identificacion = dto.identificacion().trim();
        Persona persona = personaRepository.findByIdentificacion(identificacion)
                .filter(p -> "S".equalsIgnoreCase(p.getActivo()))
                .orElseThrow(() -> new ResourceNotFoundException("Persona", identificacion));
        return crear(cuenta, persona, dto.activo(), dto.notifica());
    }

    private CuentaAsociadaDTO crear(Cuenta cuenta, Persona persona, String activo, String notifica) {
        validarNoEsTitular(cuenta, persona);
        if (cuentaAsociadaRepository.existsByCuenta_IdCuenAndPersona_IdPers(cuenta.getIdCuen(), persona.getIdPers())) {
            throw new BusinessException(MSG_YA_ASOCIADA);
        }

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
                .activo(StringUtils.hasText(activo) ? activo : "S")
                .notifica(StringUtils.hasText(notifica) ? notifica : "S")
                .build();
        CuentaAsociada guardado = cuentaAsociadaRepository.save(entity);
        entityManager.refresh(guardado);
        return toDTO(guardado);
    }

    @Override
    public CuentaAsociadaDTO update(Integer id, CuentaAsociadaDTO dto) {
        CuentaAsociada entity = getEntityOrThrow(id);
        if (!puedeModificar(entity.getCuenta())) {
            throw new ResourceNotFoundException("Cuenta asociada", id);
        }
        // Un cliente solo cambia estado y notificaciones: la cuenta y la persona se ignoran
        if (usuarioActual.esAdmin()) {
            Cuenta cuenta = getCuentaOrThrow(dto.idCuen());
            Persona persona = getPersonaOrThrow(dto.idPers());
            validarNoEsTitular(cuenta, persona);
            if (cuentaAsociadaRepository.existsByCuenta_IdCuenAndPersona_IdPersAndIdCuasNot(
                    cuenta.getIdCuen(), persona.getIdPers(), id)) {
                throw new BusinessException(MSG_YA_ASOCIADA);
            }
            entity.setCuenta(cuenta);
            entity.setPersona(persona);
        }
        entity.setActivo(StringUtils.hasText(dto.activo()) ? dto.activo() : entity.getActivo());
        entity.setNotifica(StringUtils.hasText(dto.notifica()) ? dto.notifica() : entity.getNotifica());
        return toDTO(cuentaAsociadaRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        cuentaAsociadaRepository.delete(getEntityOrThrow(id));
    }

    // Solo el titular de la cuenta (o un administrador) administra sus asociados
    private boolean puedeModificar(Cuenta cuenta) {
        return usuarioActual.esAdmin()
                || cuenta.getPropietario().getIdPers().equals(usuarioActual.idPersona());
    }

    private boolean puedeVer(CuentaAsociada entity) {
        return puedeModificar(entity.getCuenta())
                || (entity.getPersona().getIdPers().equals(usuarioActual.idPersona())
                        && "S".equalsIgnoreCase(entity.getActivo()));
    }

    private Cuenta getCuentaModificableOrThrow(Integer id) {
        Cuenta cuenta = getCuentaOrThrow(id);
        if (!puedeModificar(cuenta)) {
            throw new ResourceNotFoundException("Cuenta", id);
        }
        return cuenta;
    }

    private void validarNoEsTitular(Cuenta cuenta, Persona persona) {
        if (cuenta.getPropietario().getIdPers().equals(persona.getIdPers())) {
            throw new BusinessException(
                    "La Persona ya es titular de la cuenta, no se puede seleccionar como una cuenta asociada");
        }
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
                entity.getNotifica(),
                entity.getFechaCreacion(),
                entity.getPersona().getNombreCompleto(),
                entity.getCuenta().getPlataforma().getNombre() + " - "
                        + entity.getCuenta().getPropietario().getNombreCompleto());
    }
}

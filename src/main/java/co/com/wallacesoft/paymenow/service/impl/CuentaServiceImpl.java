package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.repository.CuentaAsociadaRepository;
import co.com.wallacesoft.paymenow.security.UsuarioActual;
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
    private final CuentaAsociadaRepository cuentaAsociadaRepository;
    private final EntityManager entityManager;
    private final UsuarioActual usuarioActual;

    @Override
    @Transactional(readOnly = true)
    public List<CuentaDTO> findAll() {
        return cuentasVisibles().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaDTO findById(Integer id) {
        Cuenta entity = getEntityOrThrow(id);
        if (!puedeVer(entity)) {
            throw new ResourceNotFoundException("Cuenta", id);
        }
        return toDTO(entity);
    }

    @Override
    public ContadorDTO count() {
        return new ContadorDTO(usuarioActual.esAdmin() ? cuentaRepository.count() : cuentasVisibles().size());
    }

    @Override
    public CuentaDTO create(CuentaDTO dto) {
        Plataforma plataforma = getPlataformaOrThrow(dto.idPlat());
        // Un cliente siempre crea la cuenta a su nombre, sin importar lo que llegue en el body
        Integer idPropietario = usuarioActual.esAdmin() ? dto.idPers() : usuarioActual.idPersona();
        Persona propietario = getPersonaOrThrow(idPropietario);

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
        if (!puedeModificar(entity)) {
            throw new ResourceNotFoundException("Cuenta", id);
        }
        entity.setPlataforma(getPlataformaOrThrow(dto.idPlat()));
        // Solo un administrador puede cambiar el titular
        if (usuarioActual.esAdmin()) {
            entity.setPropietario(getPersonaOrThrow(dto.idPers()));
        }
        entity.setActivo(StringUtils.hasText(dto.activo()) ? dto.activo() : entity.getActivo());
        entity.setFechaPago(dto.fechaPago());
        return toDTO(cuentaRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        cuentaRepository.delete(getEntityOrThrow(id));
    }

    private List<Cuenta> cuentasVisibles() {
        return usuarioActual.esAdmin()
                ? cuentaRepository.findAll()
                : cuentaRepository.findVisiblesPara(usuarioActual.idPersona());
    }

    private boolean puedeModificar(Cuenta entity) {
        return usuarioActual.esAdmin()
                || entity.getPropietario().getIdPers().equals(usuarioActual.idPersona());
    }

    private boolean puedeVer(Cuenta entity) {
        return puedeModificar(entity) || cuentaAsociadaRepository.existsByCuenta_IdCuenAndPersona_IdPersAndActivo(
                entity.getIdCuen(), usuarioActual.idPersona(), "S");
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
                entity.getFechaCreacion(),
                entity.getPropietario().getNombreCompleto(),
                entity.getPlataforma().getNombre());
    }
}

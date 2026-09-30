package co.com.wallacesoft.paymenow.service.impl;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PagosDTO;
import co.com.wallacesoft.paymenow.entity.CuentaAsociada;
import co.com.wallacesoft.paymenow.entity.EstadosPago;
import co.com.wallacesoft.paymenow.entity.Pagos;
import co.com.wallacesoft.paymenow.exception.ResourceNotFoundException;
import co.com.wallacesoft.paymenow.repository.CuentaAsociadaRepository;
import co.com.wallacesoft.paymenow.repository.EstadosPagoRepository;
import co.com.wallacesoft.paymenow.repository.PagosRepository;
import co.com.wallacesoft.paymenow.service.PagosService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PagosServiceImpl implements PagosService {

    private final PagosRepository pagosRepository;
    private final CuentaAsociadaRepository cuentaAsociadaRepository;
    private final EstadosPagoRepository estadosPagoRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<PagosDTO> findAll() {
        return pagosRepository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PagosDTO findById(Integer id) {
        return toDTO(getEntityOrThrow(id));
    }

    @Override
    public ContadorDTO count() {
        return new ContadorDTO(pagosRepository.count());
    }

    @Override
    public PagosDTO create(PagosDTO dto) {
        CuentaAsociada cuentaAsociada = getCuentaAsociadaOrThrow(dto.idCuas());
        EstadosPago estadoPago = getEstadoPagoOrThrow(dto.idEspa());

        Pagos entity = Pagos.builder()
                .fechaPago(dto.fechaPago())
                .cuentaAsociada(cuentaAsociada)
                .estadoPago(estadoPago)
                .build();
        Pagos guardado = pagosRepository.save(entity);
        entityManager.refresh(guardado);
        return toDTO(guardado);
    }

    @Override
    public PagosDTO update(Integer id, PagosDTO dto) {
        Pagos entity = getEntityOrThrow(id);
        entity.setFechaPago(dto.fechaPago());
        entity.setCuentaAsociada(getCuentaAsociadaOrThrow(dto.idCuas()));
        entity.setEstadoPago(getEstadoPagoOrThrow(dto.idEspa()));
        return toDTO(pagosRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        pagosRepository.delete(getEntityOrThrow(id));
    }

    private Pagos getEntityOrThrow(Integer id) {
        return pagosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago", id));
    }

    private CuentaAsociada getCuentaAsociadaOrThrow(Integer id) {
        return cuentaAsociadaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta asociada", id));
    }

    private EstadosPago getEstadoPagoOrThrow(Integer id) {
        return estadosPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Estado de pago", id));
    }

    private PagosDTO toDTO(Pagos entity) {
        return new PagosDTO(
                entity.getIdPago(),
                entity.getFechaPago(),
                entity.getCuentaAsociada().getIdCuas(),
                entity.getEstadoPago().getIdEspa(),
                entity.getFechaCreacion());
    }
}

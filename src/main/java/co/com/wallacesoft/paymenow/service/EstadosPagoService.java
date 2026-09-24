package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.EstadosPagoDTO;

import java.util.List;

public interface EstadosPagoService {
    List<EstadosPagoDTO> findAll();
    EstadosPagoDTO findById(Integer id);
    EstadosPagoDTO create(EstadosPagoDTO dto);
    EstadosPagoDTO update(Integer id, EstadosPagoDTO dto);
    void delete(Integer id);
}

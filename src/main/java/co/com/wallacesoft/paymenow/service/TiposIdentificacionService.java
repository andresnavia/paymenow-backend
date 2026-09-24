package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.TiposIdentificacionDTO;

import java.util.List;

public interface TiposIdentificacionService {
    List<TiposIdentificacionDTO> findAll();
    TiposIdentificacionDTO findById(Integer id);
    TiposIdentificacionDTO create(TiposIdentificacionDTO dto);
    TiposIdentificacionDTO update(Integer id, TiposIdentificacionDTO dto);
    void delete(Integer id);
}

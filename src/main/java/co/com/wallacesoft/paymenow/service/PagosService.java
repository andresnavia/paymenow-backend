package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.PagosDTO;

import java.util.List;

public interface PagosService {
    List<PagosDTO> findAll();
    PagosDTO findById(Integer id);
    PagosDTO create(PagosDTO dto);
    PagosDTO update(Integer id, PagosDTO dto);
    void delete(Integer id);
}

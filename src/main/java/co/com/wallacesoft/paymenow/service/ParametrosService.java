package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.ParametrosDTO;

import java.util.List;

public interface ParametrosService {
    List<ParametrosDTO> findAll();
    ParametrosDTO findById(Integer id);
    ParametrosDTO create(ParametrosDTO dto);
    ParametrosDTO update(Integer id, ParametrosDTO dto);
    void delete(Integer id);
}

package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.CuentaDTO;

import java.util.List;

public interface CuentaService {
    List<CuentaDTO> findAll();
    CuentaDTO findById(Integer id);
    CuentaDTO create(CuentaDTO dto);
    CuentaDTO update(Integer id, CuentaDTO dto);
    void delete(Integer id);
}

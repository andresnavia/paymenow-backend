package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.CuentaAsociadaDTO;

import java.util.List;

public interface CuentaAsociadaService {
    List<CuentaAsociadaDTO> findAll();
    CuentaAsociadaDTO findById(Integer id);
    CuentaAsociadaDTO create(CuentaAsociadaDTO dto);
    CuentaAsociadaDTO update(Integer id, CuentaAsociadaDTO dto);
    void delete(Integer id);
}

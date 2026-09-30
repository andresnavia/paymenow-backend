package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.RolDTO;

import java.util.List;

public interface RolService {
    List<RolDTO> findAll();
    RolDTO findById(Integer id);
    RolDTO create(RolDTO dto);
    RolDTO update(Integer id, RolDTO dto);
    void delete(Integer id);
}

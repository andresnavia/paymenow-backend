package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.UsuarioDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioDTO> findAll();
    UsuarioDTO findById(Integer id);
    UsuarioDTO create(UsuarioDTO dto);
    UsuarioDTO update(Integer id, UsuarioDTO dto);
    void delete(Integer id);
}

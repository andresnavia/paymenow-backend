package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PlataformaDTO;

import java.util.List;

public interface PlataformaService {
    List<PlataformaDTO> findAll();

    PlataformaDTO findById(Integer id);

    PlataformaDTO create(PlataformaDTO dto);

    PlataformaDTO update(Integer id, PlataformaDTO dto);

    void delete(Integer id);

    ContadorDTO count();
}

package co.com.wallacesoft.paymenow.service;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PersonaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonaService {
    List<PersonaDTO> findAll();

    Page<PersonaDTO> findAll(Pageable pageable);

    PersonaDTO findById(Integer id);

    PersonaDTO findByIdentificacion(String identificacion);

    PersonaDTO create(PersonaDTO dto);

    PersonaDTO update(Integer id, PersonaDTO dto);

    void delete(Integer id);

    ContadorDTO count();
}

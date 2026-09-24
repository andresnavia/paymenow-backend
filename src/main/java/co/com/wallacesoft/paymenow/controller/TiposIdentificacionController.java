package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.TiposIdentificacionDTO;
import co.com.wallacesoft.paymenow.service.TiposIdentificacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-identificacion")
@RequiredArgsConstructor
@Tag(name = "Tipos de identificacion", description = "CRUD de tipos de documento de identidad")
public class TiposIdentificacionController {

    private final TiposIdentificacionService service;

    @GetMapping
    public ResponseEntity<List<TiposIdentificacionDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TiposIdentificacionDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TiposIdentificacionDTO create(@Valid @RequestBody TiposIdentificacionDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TiposIdentificacionDTO> update(@PathVariable Integer id,
                                                          @Valid @RequestBody TiposIdentificacionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

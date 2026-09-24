package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.ParametrosDTO;
import co.com.wallacesoft.paymenow.service.ParametrosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parametros")
@RequiredArgsConstructor
@Tag(name = "Parametros", description = "CRUD de parametros de configuracion generales de la aplicacion")
public class ParametrosController {

    private final ParametrosService service;

    @GetMapping
    public ResponseEntity<List<ParametrosDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParametrosDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParametrosDTO create(@Valid @RequestBody ParametrosDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParametrosDTO> update(@PathVariable Integer id, @Valid @RequestBody ParametrosDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PagosDTO;
import co.com.wallacesoft.paymenow.service.PagosService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "CRUD del historial de pagos de cada cuenta asociada")
public class PagosController {

    private final PagosService service;

    @GetMapping
    public ResponseEntity<List<PagosDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagosDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PagosDTO create(@Valid @RequestBody PagosDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagosDTO> update(@PathVariable Integer id, @Valid @RequestBody PagosDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/contar")
    public ResponseEntity<ContadorDTO> count() {
        return ResponseEntity.ok(service.count());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

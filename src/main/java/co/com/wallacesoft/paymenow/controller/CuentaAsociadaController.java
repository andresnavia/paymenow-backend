package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.CuentaAsociadaDTO;
import co.com.wallacesoft.paymenow.service.CuentaAsociadaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas-asociadas")
@RequiredArgsConstructor
@Tag(name = "Cuentas asociadas", description = "CRUD de perfiles/pantallas asociadas a una cuenta de streaming")
public class CuentaAsociadaController {

    private final CuentaAsociadaService service;

    @GetMapping
    public ResponseEntity<List<CuentaAsociadaDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaAsociadaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaAsociadaDTO create(@Valid @RequestBody CuentaAsociadaDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaAsociadaDTO> update(@PathVariable Integer id,
                                                     @Valid @RequestBody CuentaAsociadaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

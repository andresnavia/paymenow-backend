package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.CuentaDTO;
import co.com.wallacesoft.paymenow.service.CuentaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas")
@RequiredArgsConstructor
@Tag(name = "Cuentas", description = "CRUD de cuentas de streaming que un propietario administra")
public class CuentaController {

    private final CuentaService service;

    @GetMapping
    public ResponseEntity<List<CuentaDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaDTO create(@Valid @RequestBody CuentaDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaDTO> update(@PathVariable Integer id, @Valid @RequestBody CuentaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/contar")
    public ResponseEntity<ContadorDTO> count() {
        return ResponseEntity.ok(service.count());
    }

}

package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.EstadosPagoDTO;
import co.com.wallacesoft.paymenow.service.EstadosPagoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estados-pago")
@RequiredArgsConstructor
@Tag(name = "Estados de pago", description = "CRUD de estados posibles de un pago (pendiente, pagado, vencido, etc.)")
public class EstadosPagoController {

    private final EstadosPagoService service;

    @GetMapping
    public ResponseEntity<List<EstadosPagoDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstadosPagoDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstadosPagoDTO create(@Valid @RequestBody EstadosPagoDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstadosPagoDTO> update(@PathVariable Integer id, @Valid @RequestBody EstadosPagoDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

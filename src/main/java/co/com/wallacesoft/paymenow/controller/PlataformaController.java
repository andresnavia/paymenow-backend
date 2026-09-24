package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.PlataformaDTO;
import co.com.wallacesoft.paymenow.service.PlataformaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plataformas")
@RequiredArgsConstructor
@Tag(name = "Plataformas", description = "CRUD de plataformas de streaming (Netflix, Disney+, HBO, etc.)")
public class PlataformaController {

    private final PlataformaService service;

    @GetMapping
    public ResponseEntity<List<PlataformaDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlataformaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlataformaDTO create(@Valid @RequestBody PlataformaDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlataformaDTO> update(@PathVariable Integer id, @Valid @RequestBody PlataformaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

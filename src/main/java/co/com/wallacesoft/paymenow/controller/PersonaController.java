package co.com.wallacesoft.paymenow.controller;

import co.com.wallacesoft.paymenow.dto.ContadorDTO;
import co.com.wallacesoft.paymenow.dto.PersonaDTO;
import co.com.wallacesoft.paymenow.service.PersonaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
@Tag(name = "Personas", description = "CRUD de personas (propietarios y usuarios asociados de las cuentas)")
public class PersonaController {

    private final PersonaService service;

    @GetMapping
    public ResponseEntity<List<PersonaDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/paginado")
    public ResponseEntity<Page<PersonaDTO>> findAllPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("idPers"));
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaDTO> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/contar")
    public ResponseEntity<ContadorDTO> count() {
        return ResponseEntity.ok(service.count());
    }

    @GetMapping("/identificacion/{identificacion}")
    public ResponseEntity<PersonaDTO> findByIdentificacion(@PathVariable String identificacion) {
        return ResponseEntity.ok(service.findByIdentificacion(identificacion));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaDTO create(@Valid @RequestBody PersonaDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaDTO> update(@PathVariable Integer id, @Valid @RequestBody PersonaDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

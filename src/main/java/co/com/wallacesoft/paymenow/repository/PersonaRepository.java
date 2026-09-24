package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    Optional<Persona> findByIdentificacion(String identificacion);

    boolean existsByIdentificacion(String identificacion);

    boolean existsByEmail(String email);
}

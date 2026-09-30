package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByPersona_IdPersAndRol_IdRol(Integer idPers, Integer idRol);
}

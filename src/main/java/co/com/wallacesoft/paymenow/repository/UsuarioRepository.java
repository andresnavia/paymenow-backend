package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.security.UsuarioAutenticado;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import co.com.wallacesoft.paymenow.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByPersona_IdPersAndRol_IdRol(Integer idPers, Integer idRol);

    @Query("""
            select new co.com.wallacesoft.paymenow.security.UsuarioAutenticado(u.idUsua, u.persona.idPers, u.rol.idRol)
            from Usuario u
            where u.idFirebase = :idFirebase and u.activo = 'S'
            """)
    List<UsuarioAutenticado> findActivosByIdFirebase(@Param("idFirebase") String idFirebase);
}

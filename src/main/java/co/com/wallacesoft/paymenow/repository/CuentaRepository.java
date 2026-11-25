package co.com.wallacesoft.paymenow.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import co.com.wallacesoft.paymenow.entity.Cuenta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    // Trae plataforma y propietario en la misma consulta (evita N+1 en el listado)
    @Override
    @EntityGraph(attributePaths = {"plataforma", "propietario"})
    List<Cuenta> findAll();

    List<Cuenta> findByPropietario_IdPers(Integer idPers);

    // Cuentas de las que la persona es titular o en las que esta asociada de forma activa
    @Query("""
            select c from Cuenta c
            join fetch c.plataforma
            join fetch c.propietario
            where c.propietario.idPers = :idPers
               or exists (select 1 from CuentaAsociada ca
                          where ca.cuenta = c and ca.persona.idPers = :idPers and ca.activo = 'S')
            """)
    List<Cuenta> findVisiblesPara(@Param("idPers") Integer idPers);

    List<Cuenta> findByPlataforma_IdPlat(Integer idPlat);
}

package co.com.wallacesoft.paymenow.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import co.com.wallacesoft.paymenow.entity.CuentaAsociada;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaAsociadaRepository extends JpaRepository<CuentaAsociada, Integer> {

    // Trae persona, cuenta, plataforma y titular en la misma consulta (evita N+1 en el listado)
    @Override
    @EntityGraph(attributePaths = {"persona", "cuenta", "cuenta.plataforma", "cuenta.propietario"})
    List<CuentaAsociada> findAll();

    List<CuentaAsociada> findByCuenta_IdCuen(Integer idCuen);

    List<CuentaAsociada> findByPersona_IdPers(Integer idPers);

    // Asociaciones de las cuentas de la persona, mas su propia asociacion activa en cuentas ajenas
    @EntityGraph(attributePaths = {"persona", "cuenta", "cuenta.plataforma", "cuenta.propietario"})
    @Query("""
            select ca from CuentaAsociada ca
            where ca.cuenta.propietario.idPers = :idPers
               or (ca.persona.idPers = :idPers and ca.activo = 'S')
            """)
    List<CuentaAsociada> findVisiblesPara(@Param("idPers") Integer idPers);

    boolean existsByCuenta_IdCuenAndPersona_IdPers(Integer idCuen, Integer idPers);

    boolean existsByCuenta_IdCuenAndPersona_IdPersAndActivo(Integer idCuen, Integer idPers, String activo);

    // Igual que el anterior pero ignorando un registro (el que se esta editando)
    boolean existsByCuenta_IdCuenAndPersona_IdPersAndIdCuasNot(Integer idCuen, Integer idPers, Integer idCuas);
}

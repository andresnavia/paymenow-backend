package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.CuentaAsociada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaAsociadaRepository extends JpaRepository<CuentaAsociada, Integer> {

    List<CuentaAsociada> findByCuenta_IdCuen(Integer idCuen);

    List<CuentaAsociada> findByPersona_IdPers(Integer idPers);
}

package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.Pagos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagosRepository extends JpaRepository<Pagos, Integer> {

    List<Pagos> findByCuentaAsociada_IdCuas(Integer idCuas);
}

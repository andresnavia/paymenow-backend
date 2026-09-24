package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    List<Cuenta> findByPropietario_IdPers(Integer idPers);

    List<Cuenta> findByPlataforma_IdPlat(Integer idPlat);
}

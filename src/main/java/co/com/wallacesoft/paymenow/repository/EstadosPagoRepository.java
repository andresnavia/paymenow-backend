package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.EstadosPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadosPagoRepository extends JpaRepository<EstadosPago, Integer> {
}

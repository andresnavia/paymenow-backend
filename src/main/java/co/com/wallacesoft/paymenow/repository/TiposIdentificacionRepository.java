package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.TiposIdentificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TiposIdentificacionRepository extends JpaRepository<TiposIdentificacion, Integer> {
}

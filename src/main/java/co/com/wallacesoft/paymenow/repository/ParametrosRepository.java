package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametrosRepository extends JpaRepository<Parametros, Integer> {
}

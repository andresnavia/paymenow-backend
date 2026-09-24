package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.Plataforma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlataformaRepository extends JpaRepository<Plataforma, Integer> {
}

package co.com.wallacesoft.paymenow.repository;

import co.com.wallacesoft.paymenow.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
}

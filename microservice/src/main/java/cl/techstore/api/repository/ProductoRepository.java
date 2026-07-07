package cl.techstore.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.techstore.api.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();
}

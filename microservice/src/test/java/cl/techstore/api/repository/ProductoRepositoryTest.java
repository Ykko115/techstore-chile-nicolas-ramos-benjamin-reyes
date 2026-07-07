package cl.techstore.api.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import cl.techstore.api.model.Producto;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void findByActivoTrue_deberiaRetornarSoloActivos() {
        var activo1 = new Producto(null, "TV", "TV 50", 100.0, 10, "Electrónica", true);
        var activo2 = new Producto(null, "Laptop", "Laptop gaming", 1500.0, 5, "Computación", true);
        var inactivo = new Producto(null, "Radio", "Radio vieja", 50.0, 2, "Electrónica", false);
        productoRepository.saveAll(List.of(activo1, activo2, inactivo));

        List<Producto> resultado = productoRepository.findByActivoTrue();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(Producto::getActivo));
    }

    @Test
    void findByActivoTrue_deberiaRetornarVacioSiNoHayActivos() {
        var inactivo = new Producto(null, "Radio", "Radio vieja", 50.0, 2, "Electrónica", false);
        productoRepository.save(inactivo);

        List<Producto> resultado = productoRepository.findByActivoTrue();

        assertTrue(resultado.isEmpty());
    }

    @Test
    void save_deberiaPersistirProducto() {
        var producto = new Producto(null, "TV", "TV 50", 100.0, 10, "Electrónica", true);

        Producto guardado = productoRepository.save(producto);

        assertNotNull(guardado.getId());
        assertEquals("TV", guardado.getNombre());
    }

    @Test
    void findById_deberiaRetornarProducto() {
        var producto = new Producto(null, "TV", "TV 50", 100.0, 10, "Electrónica", true);
        Producto guardado = productoRepository.save(producto);

        var encontrado = productoRepository.findById(guardado.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("TV", encontrado.get().getNombre());
    }
}

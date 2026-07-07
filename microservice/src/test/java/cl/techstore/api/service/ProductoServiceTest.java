package cl.techstore.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.techstore.api.dto.ProductoDTO;
import cl.techstore.api.model.Producto;
import cl.techstore.api.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void listarTodos_deberiaRetornarSoloActivos() {
        var activo = new Producto(1L, "TV", "TV 50", 100.0, 10, "Electrónica", true);
        var inactivo = new Producto(2L, "Radio", "Radio vieja", 50.0, 5, "Electrónica", false);
        when(productoRepository.findByActivoTrue()).thenReturn(List.of(activo));

        List<Producto> resultado = productoService.listarTodos();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(productoRepository).findByActivoTrue();
        verify(productoRepository, never()).findAll();
    }

    @Test
    void crear_deberiaGuardarYRetornarProducto() {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("Laptop");
        dto.setDescripcion("Laptop gaming");
        dto.setPrecio(1500.0);
        dto.setStock(5);
        dto.setCategoria("Computación");

        var esperado = new Producto(null, "Laptop", "Laptop gaming", 1500.0, 5, "Computación", true);
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> {
            Producto p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        Producto resultado = productoService.crear(dto);

        assertNotNull(resultado.getId());
        assertEquals("Laptop", resultado.getNombre());
        assertTrue(resultado.getActivo());
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void modificar_deberiaActualizarProducto() {
        Producto existente = new Producto(1L, "TV", "TV 50", 100.0, 10, "Electrónica", true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        ProductoDTO dto = new ProductoDTO();
        dto.setNombre("TV 4K");
        dto.setDescripcion("TV 4K 65");
        dto.setPrecio(2000.0);
        dto.setStock(3);
        dto.setCategoria("Electrónica");
        dto.setActivo(true);

        Producto resultado = productoService.modificar(1L, dto);

        assertEquals("TV 4K", resultado.getNombre());
        assertEquals(2000.0, resultado.getPrecio());
        verify(productoRepository).save(existente);
    }

    @Test
    void modificar_deberiaLanzarExcepcionSiNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productoService.modificar(99L, new ProductoDTO()));
    }

    @Test
    void eliminar_deberiaHacerSoftDelete() {
        Producto existente = new Producto(1L, "TV", "TV 50", 100.0, 10, "Electrónica", true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        Producto resultado = productoService.eliminar(1L);

        assertFalse(resultado.getActivo());
        verify(productoRepository).save(existente);
    }

    @Test
    void eliminar_deberiaLanzarExcepcionSiNoExiste() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productoService.eliminar(99L));
    }
}

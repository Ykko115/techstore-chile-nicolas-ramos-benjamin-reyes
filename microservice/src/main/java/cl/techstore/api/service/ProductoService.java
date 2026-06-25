package cl.techstore.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.techstore.api.dto.ProductoDTO;
import cl.techstore.api.model.Producto;
import cl.techstore.api.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // GET - listar todos
    public List<Producto> listarTodos(){
        return productoRepository.findAll();
    }

    // POST - crear
    public Producto crear(ProductoDTO dto){
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    // PUT - modificar
    public Producto modificar(Long id, ProductoDTO dto){
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(dto.getCategoria());
        producto.setActivo(dto.getActivo());
        return productoRepository.save(producto);
    }

    // DELETE - borrado lógico
    public Producto eliminar(Long id){
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        producto.setActivo(false);
        return productoRepository.save(producto);
    }
}

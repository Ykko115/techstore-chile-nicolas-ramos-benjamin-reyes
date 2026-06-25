package cl.techstore.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.techstore.api.dto.ProductoDTO;
import cl.techstore.api.model.Producto;
import cl.techstore.api.service.AuditoriaPublisherService;
import cl.techstore.api.service.ProductoService;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private AuditoriaPublisherService auditoriaPublisherService;

    // GET /api/productos → 200 OK
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // POST /api/productos → 201 Created
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody ProductoDTO dto) {
        Producto creado = productoService.crear(dto);
        auditoriaPublisherService.publicarEvento("CREAR", creado.getId(), creado.getNombre(), usuarioActual());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(creado);
    }

    // PUT /api/productos/{id} → 200 OK
    @PutMapping("/{id}")
    public ResponseEntity<Producto> modificar(
            @PathVariable Long id,
            @RequestBody ProductoDTO dto) {
        Producto modificado = productoService.modificar(id, dto);
        auditoriaPublisherService.publicarEvento("MODIFICAR", modificado.getId(), modificado.getNombre(), usuarioActual());
        return ResponseEntity.ok(modificado);
    }

    // DELETE /api/productos/{id} → 204 No Content
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Producto eliminado = productoService.eliminar(id);
        auditoriaPublisherService.publicarEvento("ELIMINAR", eliminado.getId(), eliminado.getNombre(), usuarioActual());
        return ResponseEntity.noContent().build();
    }

    private String usuarioActual() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "desconocido";
    }
}

package cl.techstore.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import cl.techstore.api.dto.ProductoDTO;
import cl.techstore.api.model.Producto;
import cl.techstore.api.service.AuditoriaPublisherService;
import cl.techstore.api.service.ProductoService;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    @Mock
    private ProductoService productoService;

    @Mock
    private AuditoriaPublisherService auditoriaPublisherService;

    @InjectMocks
    private ProductoController productoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@techstore.cl", null, List.of()));
    }

    @Test
    void listar_deberiaRetornar200YLista() throws Exception {
        var producto = new Producto(1L, "TV", "TV 50", 100.0, 10, "Electrónica", true);
        when(productoService.listarTodos()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("TV"))
                .andExpect(jsonPath("$[0].precio").value(100.0));
    }

    @Test
    void crear_deberiaRetornar201() throws Exception {
        var creado = new Producto(1L, "Laptop", "Laptop gaming", 1500.0, 5, "Computación", true);
        when(productoService.crear(any(ProductoDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Laptop",
                                    "descripcion": "Laptop gaming",
                                    "precio": 1500.0,
                                    "stock": 5,
                                    "categoria": "Computación"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Laptop"));
    }

    @Test
    void crear_deberiaPublicarEventoAuditoria() throws Exception {
        var creado = new Producto(1L, "Laptop", "Laptop gaming", 1500.0, 5, "Computación", true);
        when(productoService.crear(any(ProductoDTO.class))).thenReturn(creado);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "Laptop",
                                    "descripcion": "Laptop gaming",
                                    "precio": 1500.0,
                                    "stock": 5,
                                    "categoria": "Computación"
                                }
                                """))
                .andExpect(status().isCreated());

        verify(auditoriaPublisherService).publicarEvento(eq("CREAR"), eq(1L), eq("Laptop"), anyString());
    }

    @Test
    void modificar_deberiaRetornar200() throws Exception {
        var modificado = new Producto(1L, "TV 4K", "TV 4K 65", 2000.0, 3, "Electrónica", true);
        when(productoService.modificar(eq(1L), any(ProductoDTO.class))).thenReturn(modificado);

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nombre": "TV 4K",
                                    "descripcion": "TV 4K 65",
                                    "precio": 2000.0,
                                    "stock": 3,
                                    "categoria": "Electrónica",
                                    "activo": true
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("TV 4K"));
    }

    @Test
    void eliminar_deberiaRetornar204() throws Exception {
        var eliminado = new Producto(1L, "TV", "TV 50", 100.0, 10, "Electrónica", false);
        when(productoService.eliminar(1L)).thenReturn(eliminado);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void listar_deberiaRetornarListaVacia() throws Exception {
        when(productoService.listarTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}

package com.cesde.barberia.controlador;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Servicio;
import com.cesde.barberia.servicio.SServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = "*")
@Tag(name = "Servicio para la gestion de servicios de la barberia", description = "Servicios CRUD de servicios")
public class CServicio {

    private final SServicio sServicio;

    public CServicio(SServicio sServicio) {
        this.sServicio = sServicio;
    }

    @GetMapping
    @Operation(summary = "Metodo para consultar todos los servicios", description = "Este servicio devuelve el catalogo completo de servicios con precios y duraciones")
    public ResponseEntity<?> consultarTodos() {
        try {
            return ResponseEntity.ok(sServicio.consultarTodos());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar los servicios.");
        }
    }

    @GetMapping("/{codservicio}")
    @Operation(summary = "Metodo para consultar un servicio por llave primaria", description = "Este servicio consulta un servicio por su codigo (codservicio)")
    public ResponseEntity<?> consultarPorId(@PathVariable Integer codservicio) {
        try {
            return ResponseEntity.ok(sServicio.consultarPorId(codservicio));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar el servicio.");
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Metodo para buscar servicios por nombre", description = "Este servicio busca servicios que coincidan parcialmente con el nombre")
    public ResponseEntity<?> consultarPorNombre(@RequestParam(defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok(sServicio.consultarPorNombre(nombre));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al buscar servicios.");
        }
    }

    @PostMapping
    @Operation(summary = "Metodo para adicionar un servicio", description = "Este servicio agrega un nuevo servicio verificando que el nombre no se repita")
    public ResponseEntity<?> adicionar(@RequestBody Servicio servicio) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sServicio.adicionar(servicio));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al adicionar el servicio.");
        }
    }

    @PutMapping("/{codservicio}")
    @Operation(summary = "Metodo para modificar un servicio", description = "Este servicio actualiza el nombre, precio, duracion o estado de un servicio")
    public ResponseEntity<?> modificar(@PathVariable Integer codservicio, @RequestBody Servicio servicio) {
        try {
            return ResponseEntity.ok(sServicio.modificar(codservicio, servicio));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al modificar el servicio.");
        }
    }

    @DeleteMapping("/{codservicio}")
    @Operation(summary = "Metodo para eliminar un servicio", description = "Este servicio elimina un servicio si no tiene citas asociadas")
    public ResponseEntity<?> eliminar(@PathVariable Integer codservicio) {
        try {
            sServicio.eliminar(codservicio);
            return ResponseEntity.ok("Servicio eliminado con exito.");
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el servicio.");
        }
    }
}

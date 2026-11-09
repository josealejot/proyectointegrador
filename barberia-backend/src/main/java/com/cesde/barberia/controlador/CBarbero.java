package com.cesde.barberia.controlador;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Barbero;
import com.cesde.barberia.servicio.SBarbero;
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
@RequestMapping("/barberos")
@CrossOrigin(origins = "*")
@Tag(name = "Servicio para la gestion de barberos", description = "Servicios CRUD de barberos")
public class CBarbero {

    private final SBarbero sBarbero;

    public CBarbero(SBarbero sBarbero) {
        this.sBarbero = sBarbero;
    }

    @GetMapping
    @Operation(summary = "Consulta general de barberos")
    public ResponseEntity<?> consultarTodos() {
        try {
            return ResponseEntity.ok(sBarbero.consultarTodos());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar los barberos.");
        }
    }

    @GetMapping("/{idbarbero}")
    @Operation(summary = "Consulta un barbero por llave primaria")
    public ResponseEntity<?> consultarPorId(@PathVariable String idbarbero) {
        try {
            return ResponseEntity.ok(sBarbero.consultarPorId(idbarbero));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar el barbero.");
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca barberos por parte del nombre")
    public ResponseEntity<?> consultarPorNombre(@RequestParam(defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok(sBarbero.consultarPorNombre(nombre));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al buscar barberos.");
        }
    }

    @PostMapping
    @Operation(summary = "Adiciona un barbero")
    public ResponseEntity<?> adicionar(@RequestBody Barbero barbero) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sBarbero.adicionar(barbero));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al adicionar el barbero.");
        }
    }

    @PutMapping("/{idbarbero}")
    @Operation(summary = "Modifica un barbero")
    public ResponseEntity<?> modificar(@PathVariable String idbarbero, @RequestBody Barbero barbero) {
        try {
            return ResponseEntity.ok(sBarbero.modificar(idbarbero, barbero));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al modificar el barbero.");
        }
    }

    @DeleteMapping("/{idbarbero}")
    @Operation(summary = "Elimina un barbero", description = "No permite eliminar barberos con citas")
    public ResponseEntity<?> eliminar(@PathVariable String idbarbero) {
        try {
            sBarbero.eliminar(idbarbero);
            return ResponseEntity.ok("Barbero eliminado con exito.");
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el barbero.");
        }
    }
}

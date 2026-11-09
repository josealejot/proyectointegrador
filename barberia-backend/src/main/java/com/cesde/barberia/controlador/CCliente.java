package com.cesde.barberia.controlador;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Cliente;
import com.cesde.barberia.servicio.SCliente;
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
@RequestMapping("/clientes")
@CrossOrigin(origins = "*")
@Tag(name = "Servicio para la gestion de clientes", description = "Servicios CRUD de clientes")
public class CCliente {

    private final SCliente sCliente;

    public CCliente(SCliente sCliente) {
        this.sCliente = sCliente;
    }

    @GetMapping
    @Operation(summary = "Consulta general de clientes")
    public ResponseEntity<?> consultarTodos() {
        try {
            return ResponseEntity.ok(sCliente.consultarTodos());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar los clientes.");
        }
    }

    @GetMapping("/{idcliente}")
    @Operation(summary = "Consulta un cliente por llave primaria")
    public ResponseEntity<?> consultarPorId(@PathVariable String idcliente) {
        try {
            return ResponseEntity.ok(sCliente.consultarPorId(idcliente));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar el cliente.");
        }
    }

    @GetMapping("/buscar")
    @Operation(summary = "Busca clientes por parte del nombre")
    public ResponseEntity<?> consultarPorNombre(@RequestParam(defaultValue = "") String nombre) {
        try {
            return ResponseEntity.ok(sCliente.consultarPorNombre(nombre));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al buscar clientes.");
        }
    }

    @PostMapping
    @Operation(summary = "Adiciona un cliente", description = "Valida los datos y guarda el cliente")
    public ResponseEntity<?> adicionar(@RequestBody Cliente cliente) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sCliente.adicionar(cliente));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al adicionar el cliente.");
        }
    }

    @PutMapping("/{idcliente}")
    @Operation(summary = "Modifica un cliente")
    public ResponseEntity<?> modificar(@PathVariable String idcliente, @RequestBody Cliente cliente) {
        try {
            return ResponseEntity.ok(sCliente.modificar(idcliente, cliente));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al modificar el cliente.");
        }
    }

    @DeleteMapping("/{idcliente}")
    @Operation(summary = "Elimina un cliente", description = "No permite eliminar clientes con citas")
    public ResponseEntity<?> eliminar(@PathVariable String idcliente) {
        try {
            sCliente.eliminar(idcliente);
            return ResponseEntity.ok("Cliente eliminado con exito.");
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el cliente.");
        }
    }
}

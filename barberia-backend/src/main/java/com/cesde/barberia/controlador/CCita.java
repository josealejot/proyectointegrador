package com.cesde.barberia.controlador;

import com.cesde.barberia.dto.CitaRegistroDTO;
import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.servicio.SCita;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;

@RestController
@RequestMapping("/citas")
@CrossOrigin(origins = "*")
@Tag(name = "Servicio para la gestion de citas", description = "Agenda de citas de la barberia")
public class CCita {

    private final SCita sCita;

    public CCita(SCita sCita) {
        this.sCita = sCita;
    }

    @GetMapping
    @Operation(summary = "Consulta general de citas")
    public ResponseEntity<?> consultarTodas() {
        try {
            return ResponseEntity.ok(sCita.consultarTodas());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar las citas.");
        }
    }

    @GetMapping("/{codcita}")
    @Operation(summary = "Consulta una cita por llave primaria")
    public ResponseEntity<?> consultarPorId(@PathVariable Integer codcita) {
        try {
            return ResponseEntity.ok(sCita.consultarPorId(codcita));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar la cita.");
        }
    }

    @GetMapping("/filtrar")
    @Operation(summary = "Filtra citas por fecha, barbero y estado", description = "Los tres parametros son opcionales")
    public ResponseEntity<?> filtrar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) String idbarbero,
            @RequestParam(required = false) String estado) {
        try {
            return ResponseEntity.ok(sCita.filtrar(fecha, idbarbero, estado));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al filtrar las citas.");
        }
    }

    @GetMapping("/resumen-barberos")
    @Operation(summary = "Citas atendidas e ingresos por barbero", description = "Consulta nativa con proyeccion y mapper")
    public ResponseEntity<?> resumenPorBarbero() {
        try {
            return ResponseEntity.ok(sCita.resumenPorBarbero());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar el resumen.");
        }
    }

    @PostMapping
    @Operation(summary = "Agenda una cita", description = "El cliente, el barbero y el servicio deben existir")
    public ResponseEntity<?> adicionar(@RequestBody CitaRegistroDTO cita) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sCita.adicionar(cita));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al adicionar la cita.");
        }
    }

    @PutMapping("/{codcita}")
    @Operation(summary = "Modifica una cita")
    public ResponseEntity<?> modificar(@PathVariable Integer codcita, @RequestBody CitaRegistroDTO cita) {
        try {
            return ResponseEntity.ok(sCita.modificar(codcita, cita));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al modificar la cita.");
        }
    }

    @PutMapping("/{codcita}/estado")
    @Operation(summary = "Marca el estado de una cita (Pendiente, Atendida, Incumplida, Cancelada)")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer codcita, @RequestParam String estado) {
        try {
            return ResponseEntity.ok(sCita.cambiarEstado(codcita, estado));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al cambiar el estado.");
        }
    }

    @PostMapping("/{codcita}/servicios/{codservicio}")
    @Operation(summary = "Adiciona un servicio extra a una cita durante la atencion")
    public ResponseEntity<?> agregarServicioExtra(@PathVariable Integer codcita, @PathVariable Integer codservicio) {
        try {
            return ResponseEntity.ok(sCita.agregarServicioExtra(codcita, codservicio));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al agregar el servicio.");
        }
    }

    @DeleteMapping("/{codcita}")
    @Operation(summary = "Elimina una cita")
    public ResponseEntity<?> eliminar(@PathVariable Integer codcita) {
        try {
            sCita.eliminar(codcita);
            return ResponseEntity.ok("Cita eliminada con exito.");
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar la cita.");
        }
    }
}

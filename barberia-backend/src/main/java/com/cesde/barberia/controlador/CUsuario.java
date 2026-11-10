package com.cesde.barberia.controlador;

import com.cesde.barberia.dto.UsuarioRegistroDTO;
import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.servicio.SUsuario;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Servicio para la gestion de usuarios", description = "Servicios CRUD de los usuarios del sistema")
public class CUsuario {

    private final SUsuario sUsuario;

    public CUsuario(SUsuario sUsuario) {
        this.sUsuario = sUsuario;
    }

    @GetMapping
    @Operation(summary = "Metodo para consultar todos los usuarios", description = "Este servicio devuelve todos los usuarios sin exponer la contrasena")
    public ResponseEntity<?> consultarTodos() {
        try {
            return ResponseEntity.ok(sUsuario.consultarTodos());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar los usuarios.");
        }
    }

    @GetMapping("/{idusuario}")
    @Operation(summary = "Metodo para consultar un usuario por llave primaria", description = "Este servicio consulta un usuario por su identificador (idusuario)")
    public ResponseEntity<?> consultarPorId(@PathVariable Integer idusuario) {
        try {
            return ResponseEntity.ok(sUsuario.consultarPorId(idusuario));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar el usuario.");
        }
    }

    @PostMapping
    @Operation(summary = "Metodo para adicionar un usuario", description = "Este servicio registra un nuevo usuario cifrando su contrasena con BCrypt")
    public ResponseEntity<?> adicionar(@RequestBody UsuarioRegistroDTO usuario) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sUsuario.adicionar(usuario));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al adicionar el usuario.");
        }
    }

    @PutMapping("/{idusuario}")
    @Operation(summary = "Metodo para modificar un usuario", description = "Este servicio actualiza los datos de un usuario; la contrasena solo cambia si se envia una nueva")
    public ResponseEntity<?> modificar(@PathVariable Integer idusuario, @RequestBody UsuarioRegistroDTO usuario) {
        try {
            return ResponseEntity.ok(sUsuario.modificar(idusuario, usuario));
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al modificar el usuario.");
        }
    }

    @DeleteMapping("/{idusuario}")
    @Operation(summary = "Metodo para eliminar un usuario", description = "Este servicio elimina un usuario asegurando que siempre quede al menos un administrador activo")
    public ResponseEntity<?> eliminar(@PathVariable Integer idusuario) {
        try {
            sUsuario.eliminar(idusuario);
            return ResponseEntity.ok("Usuario eliminado con exito.");
        } catch (NoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al eliminar el usuario.");
        }
    }
}

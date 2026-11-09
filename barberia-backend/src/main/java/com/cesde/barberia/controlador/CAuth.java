package com.cesde.barberia.controlador;

import com.cesde.barberia.dto.LoginDTO;
import com.cesde.barberia.dto.UsuarioRegistroDTO;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Catalogos;
import com.cesde.barberia.servicio.SUsuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Servicio de acceso", description = "Registro, inicio de sesion y listas de opciones")
public class CAuth {

    private final SUsuario sUsuario;

    public CAuth(SUsuario sUsuario) {
        this.sUsuario = sUsuario;
    }

    @PostMapping("/registro")
    @Operation(summary = "Crea una cuenta nueva", description = "La cuenta queda con rol Recepcionista")
    public ResponseEntity<?> registrar(@RequestBody UsuarioRegistroDTO usuario) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(sUsuario.registrar(usuario));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al registrar el usuario.");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesion con correo y contrasena")
    public ResponseEntity<?> iniciarSesion(@RequestBody LoginDTO datos) {
        try {
            return ResponseEntity.ok(sUsuario.iniciarSesion(datos));
        } catch (ReglaNegocioException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al iniciar sesion.");
        }
    }

    @GetMapping("/catalogos")
    @Operation(summary = "Listas de opciones conocidas", description = "Las usa el front en listas desplegables y botones de radio")
    public ResponseEntity<?> catalogos() {
        try {
            Map<String, List<String>> catalogos = Map.of(
                    "especialidades", Catalogos.ESPECIALIDADES,
                    "metodosPago", Catalogos.METODOS_PAGO,
                    "estadosCita", Catalogos.ESTADOS_CITA,
                    "roles", Catalogos.ROLES);
            return ResponseEntity.ok(catalogos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al consultar los catalogos.");
        }
    }
}

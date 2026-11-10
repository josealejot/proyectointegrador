package com.cesde.barberia.servicio;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Barbero;
import com.cesde.barberia.modelo.Catalogos;
import com.cesde.barberia.modelo.Usuario;
import com.cesde.barberia.repositorio.IBarbero;
import com.cesde.barberia.repositorio.ICita;
import com.cesde.barberia.repositorio.IUsuario;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SBarbero {

    private final IBarbero iBarbero;
    private final ICita iCita;
    private final IUsuario iUsuario;
    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    public SBarbero(IBarbero iBarbero, ICita iCita, IUsuario iUsuario) {
        this.iBarbero = iBarbero;
        this.iCita = iCita;
        this.iUsuario = iUsuario;
    }

    public List<Barbero> consultarTodos() {
        try {
            return iBarbero.findAll();
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar los barberos.");
        }
    }

    public Barbero consultarPorId(String idbarbero) {
        try {
            return iBarbero.findById(idbarbero)
                    .orElseThrow(() -> new NoEncontradoException("Barbero no registrado."));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar el barbero.");
        }
    }

    public List<Barbero> consultarPorNombre(String nombre) {
        try {
            return iBarbero.findByNombarberoContainingIgnoreCase(nombre == null ? "" : nombre.trim());
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al buscar barberos.");
        }
    }

    public Barbero adicionar(Barbero barbero) {
        try {
            validar(barbero);
            if (iBarbero.existsById(barbero.getIdbarbero())) {
                throw new ReglaNegocioException("Ya existe un barbero con esa identificacion.");
            }
            Barbero guardado = iBarbero.save(barbero);
            sincronizarCuentaAcceso(guardado, barbero.getCorreo(), barbero.getContrasena());
            return guardado;
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al adicionar el barbero.");
        }
    }

    public Barbero modificar(String idbarbero, Barbero datos) {
        try {
            Barbero barbero = consultarPorId(idbarbero);
            datos.setIdbarbero(idbarbero);
            validar(datos);
            barbero.setNombarbero(datos.getNombarbero());
            barbero.setTelbarbero(datos.getTelbarbero());
            barbero.setEspecialidad(datos.getEspecialidad());
            barbero.setActivo(datos.getActivo());
            Barbero guardado = iBarbero.save(barbero);
            sincronizarCuentaAcceso(guardado, datos.getCorreo(), datos.getContrasena());
            return guardado;
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al modificar el barbero.");
        }
    }

    private void sincronizarCuentaAcceso(Barbero barbero, String correo, String contrasena) {
        Optional<Usuario> existente = iUsuario.findByIdentificacion(barbero.getIdbarbero());
        if (contrasena != null && !contrasena.trim().isEmpty()) {
            if (contrasena.length() < 8 || contrasena.length() > 72) {
                throw new ReglaNegocioException("La contrasena debe tener entre 8 y 72 caracteres.");
            }
            String correoLimpio = (correo != null && !correo.isBlank())
                    ? Validador.correo(correo)
                    : (existente.map(Usuario::getCorreo).orElse(barbero.getIdbarbero() + "@barberia.com"));
            Usuario u = existente.orElseGet(Usuario::new);
            u.setNombre(barbero.getNombarbero());
            u.setCorreo(correoLimpio);
            u.setContrasena(cifrador.encode(contrasena));
            u.setRol("Barbero");
            u.setActivo(barbero.getActivo());
            u.setIdentificacion(barbero.getIdbarbero());
            iUsuario.save(u);
        } else if (existente.isPresent()) {
            Usuario u = existente.get();
            u.setNombre(barbero.getNombarbero());
            if (correo != null && !correo.isBlank()) {
                u.setCorreo(Validador.correo(correo));
            }
            u.setActivo(barbero.getActivo());
            u.setIdentificacion(barbero.getIdbarbero());
            iUsuario.save(u);
        }
    }

    public void eliminar(String idbarbero) {
        try {
            Barbero barbero = consultarPorId(idbarbero);
            // Integridad referencial: no se elimina un barbero que tenga citas
            if (iCita.existsByBarbero_Idbarbero(idbarbero)) {
                throw new ReglaNegocioException(
                        "No se puede eliminar el barbero porque tiene citas registradas.");
            }
            iBarbero.delete(barbero);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al eliminar el barbero.");
        }
    }

    private void validar(Barbero barbero) {
        if (barbero == null) {
            throw new ReglaNegocioException("Debe enviar los datos del barbero.");
        }
        barbero.setIdbarbero(Validador.digitos(barbero.getIdbarbero(), "identificacion", 6, 10));
        barbero.setNombarbero(Validador.texto(barbero.getNombarbero(), "nombre", 3, 80));
        barbero.setTelbarbero(Validador.digitos(barbero.getTelbarbero(), "telefono", 10, 10));
        barbero.setEspecialidad(
                Validador.opcion(barbero.getEspecialidad(), "especialidad", Catalogos.ESPECIALIDADES));
        barbero.setActivo(Validador.logico(barbero.getActivo(), "activo"));
    }
}

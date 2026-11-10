package com.cesde.barberia.servicio;

import com.cesde.barberia.dto.LoginDTO;
import com.cesde.barberia.dto.UsuarioDTO;
import com.cesde.barberia.dto.UsuarioRegistroDTO;
import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.mapper.IUsuarioMapper;
import com.cesde.barberia.modelo.Barbero;
import com.cesde.barberia.modelo.Catalogos;
import com.cesde.barberia.modelo.Cliente;
import com.cesde.barberia.modelo.Usuario;
import com.cesde.barberia.repositorio.IBarbero;
import com.cesde.barberia.repositorio.ICliente;
import com.cesde.barberia.repositorio.IUsuario;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SUsuario {

    private static final String ROL_ADMIN = "Administrador";
    private static final String ROL_POR_DEFECTO = "Cliente";

    private final IUsuario iUsuario;
    private final ICliente iCliente;
    private final IBarbero iBarbero;
    private final IUsuarioMapper iUsuarioMapper;
    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    public SUsuario(IUsuario iUsuario, ICliente iCliente, IBarbero iBarbero, IUsuarioMapper iUsuarioMapper) {
        this.iUsuario = iUsuario;
        this.iCliente = iCliente;
        this.iBarbero = iBarbero;
        this.iUsuarioMapper = iUsuarioMapper;
    }

    public List<UsuarioDTO> consultarTodos() {
        try {
            return iUsuarioMapper.toDTOList(iUsuario.findAll());
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar los usuarios.");
        }
    }

    public UsuarioDTO consultarPorId(Integer idusuario) {
        try {
            return iUsuarioMapper.toDTO(buscar(idusuario));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar el usuario.");
        }
    }

    // Registro publico desde la pantalla "Crear cuenta": siempre queda con rol Cliente
    @Transactional
    public UsuarioDTO registrar(UsuarioRegistroDTO datos) {
        if (datos == null) {
            throw new ReglaNegocioException("Debe enviar los datos del usuario.");
        }
        return adicionar(new UsuarioRegistroDTO(
                datos.nombre(),
                datos.correo(),
                datos.contrasena(),
                ROL_POR_DEFECTO,
                true,
                datos.identificacion(),
                datos.telefono(),
                null));
    }

    // Creacion desde el modulo de usuarios o registro: permite elegir rol, estado y vincular con cliente/barbero
    @Transactional
    public UsuarioDTO adicionar(UsuarioRegistroDTO datos) {
        try {
            if (datos == null) {
                throw new ReglaNegocioException("Debe enviar los datos del usuario.");
            }
            Usuario usuario = new Usuario();
            usuario.setNombre(Validador.texto(datos.nombre(), "nombre", 3, 80));
            usuario.setCorreo(Validador.correo(datos.correo()));
            usuario.setRol(Validador.opcion(datos.rol(), "rol", Catalogos.ROLES));
            usuario.setActivo(Validador.logico(datos.activo() != null ? datos.activo() : true, "activo"));
            verificarCorreoUnico(usuario.getCorreo(), null);
            usuario.setContrasena(cifrador.encode(validarContrasena(datos.contrasena())));

            String idLimpia = sincronizarPerfilSegunRol(usuario, datos);
            usuario.setIdentificacion(idLimpia);

            return iUsuarioMapper.toDTO(iUsuario.save(usuario));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al registrar el usuario.");
        }
    }

    @Transactional
    public UsuarioDTO modificar(Integer idusuario, UsuarioRegistroDTO datos) {
        try {
            if (datos == null) {
                throw new ReglaNegocioException("Debe enviar los datos del usuario.");
            }
            Usuario usuario = buscar(idusuario);
            String nombre = Validador.texto(datos.nombre(), "nombre", 3, 80);
            String correo = Validador.correo(datos.correo());
            String rol = Validador.opcion(datos.rol(), "rol", Catalogos.ROLES);
            Boolean activo = Validador.logico(datos.activo(), "activo");
            verificarCorreoUnico(correo, idusuario);

            // Regla: siempre debe quedar al menos un administrador activo
            boolean eraAdminActivo = ROL_ADMIN.equals(usuario.getRol()) && usuario.getActivo();
            boolean sigueAdminActivo = ROL_ADMIN.equals(rol) && activo;
            if (eraAdminActivo && !sigueAdminActivo && iUsuario.countByRolAndActivoTrue(ROL_ADMIN) <= 1) {
                throw new ReglaNegocioException("Debe quedar al menos un administrador activo.");
            }

            usuario.setNombre(nombre);
            usuario.setCorreo(correo);
            usuario.setRol(rol);
            usuario.setActivo(activo);
            // La contrasena solo cambia si llega una nueva
            if (datos.contrasena() != null && !datos.contrasena().isEmpty()) {
                usuario.setContrasena(cifrador.encode(validarContrasena(datos.contrasena())));
            }

            String idLimpia = sincronizarPerfilSegunRol(usuario, datos);
            usuario.setIdentificacion(idLimpia);

            return iUsuarioMapper.toDTO(iUsuario.save(usuario));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al modificar el usuario.");
        }
    }

    private String sincronizarPerfilSegunRol(Usuario usuario, UsuarioRegistroDTO datos) {
        String idRaw = datos.identificacion() != null && !datos.identificacion().isBlank()
                ? datos.identificacion().trim()
                : usuario.getIdentificacion();

        if ("Cliente".equals(usuario.getRol())) {
            String cedula = idRaw != null && !idRaw.isBlank()
                    ? Validador.digitos(idRaw, "identificacion", 6, 10)
                    : null;
            if (cedula != null) {
                Cliente cliente = iCliente.findById(cedula).orElseGet(Cliente::new);
                cliente.setIdcliente(cedula);
                cliente.setNomcliente(usuario.getNombre());
                cliente.setCorreo(usuario.getCorreo());
                String tel = datos.telefono() != null && !datos.telefono().isBlank()
                        ? Validador.digitos(datos.telefono(), "telefono", 10, 10)
                        : (cliente.getTelcliente() != null ? cliente.getTelcliente() : "3000000000");
                cliente.setTelcliente(tel);
                cliente.setActivo(usuario.getActivo());
                iCliente.save(cliente);
            }
            return cedula;
        }

        if ("Barbero".equals(usuario.getRol())) {
            String cedula = idRaw != null && !idRaw.isBlank()
                    ? Validador.digitos(idRaw, "identificacion", 6, 10)
                    : null;
            if (cedula != null) {
                Barbero barbero = iBarbero.findById(cedula).orElseGet(Barbero::new);
                barbero.setIdbarbero(cedula);
                barbero.setNombarbero(usuario.getNombre());
                String tel = datos.telefono() != null && !datos.telefono().isBlank()
                        ? Validador.digitos(datos.telefono(), "telefono", 10, 10)
                        : (barbero.getTelbarbero() != null ? barbero.getTelbarbero() : "3000000000");
                barbero.setTelbarbero(tel);
                String esp = datos.especialidad() != null && Catalogos.ESPECIALIDADES.contains(datos.especialidad().trim())
                        ? datos.especialidad().trim()
                        : (barbero.getEspecialidad() != null ? barbero.getEspecialidad() : Catalogos.ESPECIALIDADES.get(0));
                barbero.setEspecialidad(esp);
                barbero.setActivo(usuario.getActivo());
                iBarbero.save(barbero);
            }
            return cedula;
        }

        return idRaw != null && !idRaw.isBlank() ? idRaw : null;
    }

    public void eliminar(Integer idusuario) {
        try {
            Usuario usuario = buscar(idusuario);
            if (ROL_ADMIN.equals(usuario.getRol()) && usuario.getActivo()
                    && iUsuario.countByRolAndActivoTrue(ROL_ADMIN) <= 1) {
                throw new ReglaNegocioException("No se puede eliminar el unico administrador activo.");
            }
            iUsuario.delete(usuario);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al eliminar el usuario.");
        }
    }

    // Inicio de sesion: compara la contrasena recibida con la cifrada
    public UsuarioDTO iniciarSesion(LoginDTO datos) {
        try {
            if (datos == null || datos.correo() == null || datos.contrasena() == null) {
                throw new ReglaNegocioException("Debe enviar el correo y la contrasena.");
            }
            Optional<Usuario> encontrado = iUsuario.findByCorreoIgnoreCase(datos.correo().trim());
            if (encontrado.isEmpty()
                    || !cifrador.matches(datos.contrasena(), encontrado.get().getContrasena())
                    || !encontrado.get().getActivo()) {
                throw new ReglaNegocioException("Correo o contrasena incorrectos.");
            }
            return iUsuarioMapper.toDTO(encontrado.get());
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al iniciar sesion.");
        }
    }

    private Usuario buscar(Integer idusuario) {
        return iUsuario.findById(idusuario)
                .orElseThrow(() -> new NoEncontradoException("Usuario no registrado."));
    }

    private String validarContrasena(String contrasena) {
        if (contrasena == null || contrasena.length() < 8 || contrasena.length() > 72) {
            throw new ReglaNegocioException("La contrasena debe tener entre 8 y 72 caracteres.");
        }
        return contrasena;
    }

    private void verificarCorreoUnico(String correo, Integer idusuarioActual) {
        Optional<Usuario> existente = iUsuario.findByCorreoIgnoreCase(correo);
        if (existente.isPresent() && !existente.get().getIdusuario().equals(idusuarioActual)) {
            throw new ReglaNegocioException("Ya existe un usuario con ese correo.");
        }
    }
}

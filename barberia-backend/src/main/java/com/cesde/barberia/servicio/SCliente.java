package com.cesde.barberia.servicio;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Cliente;
import com.cesde.barberia.modelo.Usuario;
import com.cesde.barberia.repositorio.ICita;
import com.cesde.barberia.repositorio.ICliente;
import com.cesde.barberia.repositorio.IUsuario;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SCliente {

    private final ICliente iCliente;
    private final ICita iCita;
    private final IUsuario iUsuario;
    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    public SCliente(ICliente iCliente, ICita iCita, IUsuario iUsuario) {
        this.iCliente = iCliente;
        this.iCita = iCita;
        this.iUsuario = iUsuario;
    }

    public List<Cliente> consultarTodos() {
        try {
            return iCliente.findAll();
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar los clientes.");
        }
    }

    public Cliente consultarPorId(String idcliente) {
        try {
            return iCliente.findById(idcliente)
                    .orElseThrow(() -> new NoEncontradoException("Cliente no registrado."));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar el cliente.");
        }
    }

    public List<Cliente> consultarPorNombre(String nombre) {
        try {
            return iCliente.findByNomclienteContainingIgnoreCase(nombre == null ? "" : nombre.trim());
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al buscar clientes.");
        }
    }

    public Cliente adicionar(Cliente cliente) {
        try {
            validar(cliente);
            if (iCliente.existsById(cliente.getIdcliente())) {
                throw new ReglaNegocioException("Ya existe un cliente con esa identificacion.");
            }
            Cliente guardado = iCliente.save(cliente);
            sincronizarCuentaAcceso(guardado, cliente.getContrasena());
            return guardado;
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al adicionar el cliente.");
        }
    }

    public Cliente modificar(String idcliente, Cliente datos) {
        try {
            Cliente cliente = consultarPorId(idcliente);
            datos.setIdcliente(idcliente);
            validar(datos);
            cliente.setNomcliente(datos.getNomcliente());
            cliente.setTelcliente(datos.getTelcliente());
            cliente.setCorreo(datos.getCorreo());
            cliente.setActivo(datos.getActivo());
            Cliente guardado = iCliente.save(cliente);
            sincronizarCuentaAcceso(guardado, datos.getContrasena());
            return guardado;
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al modificar el cliente.");
        }
    }

    private void sincronizarCuentaAcceso(Cliente cliente, String contrasena) {
        Optional<Usuario> existente = iUsuario.findByIdentificacion(cliente.getIdcliente())
                .or(() -> iUsuario.findByCorreoIgnoreCase(cliente.getCorreo()));
        if (contrasena != null && !contrasena.trim().isEmpty()) {
            if (contrasena.length() < 8 || contrasena.length() > 72) {
                throw new ReglaNegocioException("La contrasena debe tener entre 8 y 72 caracteres.");
            }
            Usuario u = existente.orElseGet(Usuario::new);
            u.setNombre(cliente.getNomcliente());
            u.setCorreo(cliente.getCorreo());
            u.setContrasena(cifrador.encode(contrasena));
            if (u.getRol() == null) {
                u.setRol("Cliente");
            }
            u.setActivo(cliente.getActivo());
            u.setIdentificacion(cliente.getIdcliente());
            iUsuario.save(u);
        } else if (existente.isPresent()) {
            Usuario u = existente.get();
            u.setNombre(cliente.getNomcliente());
            u.setCorreo(cliente.getCorreo());
            u.setActivo(cliente.getActivo());
            u.setIdentificacion(cliente.getIdcliente());
            iUsuario.save(u);
        }
    }

    public void eliminar(String idcliente) {
        try {
            Cliente cliente = consultarPorId(idcliente);
            // Integridad referencial: no se elimina un cliente que tenga citas
            if (iCita.existsByCliente_Idcliente(idcliente)) {
                throw new ReglaNegocioException(
                        "No se puede eliminar el cliente porque tiene citas registradas.");
            }
            iCliente.delete(cliente);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al eliminar el cliente.");
        }
    }

    // Valida tipo y extension de cada campo segun la tabla cliente
    private void validar(Cliente cliente) {
        if (cliente == null) {
            throw new ReglaNegocioException("Debe enviar los datos del cliente.");
        }
        cliente.setIdcliente(Validador.digitos(cliente.getIdcliente(), "identificacion", 6, 10));
        cliente.setNomcliente(Validador.texto(cliente.getNomcliente(), "nombre", 3, 80));
        cliente.setTelcliente(Validador.digitos(cliente.getTelcliente(), "telefono", 10, 10));
        cliente.setCorreo(Validador.correo(cliente.getCorreo()));
        cliente.setActivo(Validador.logico(cliente.getActivo(), "activo"));
    }
}

package com.cesde.barberia.servicio;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Cliente;
import com.cesde.barberia.repositorio.ICita;
import com.cesde.barberia.repositorio.ICliente;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SCliente {

    private final ICliente iCliente;
    private final ICita iCita;

    public SCliente(ICliente iCliente, ICita iCita) {
        this.iCliente = iCliente;
        this.iCita = iCita;
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
            return iCliente.save(cliente);
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
            return iCliente.save(cliente);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al modificar el cliente.");
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

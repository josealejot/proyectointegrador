package com.cesde.barberia.servicio;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Barbero;
import com.cesde.barberia.modelo.Catalogos;
import com.cesde.barberia.repositorio.IBarbero;
import com.cesde.barberia.repositorio.ICita;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SBarbero {

    private final IBarbero iBarbero;
    private final ICita iCita;

    public SBarbero(IBarbero iBarbero, ICita iCita) {
        this.iBarbero = iBarbero;
        this.iCita = iCita;
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
            return iBarbero.save(barbero);
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
            return iBarbero.save(barbero);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al modificar el barbero.");
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

package com.cesde.barberia.servicio;

import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.modelo.Servicio;
import com.cesde.barberia.repositorio.ICita;
import com.cesde.barberia.repositorio.IServicio;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SServicio {

    private final IServicio iServicio;
    private final ICita iCita;

    public SServicio(IServicio iServicio, ICita iCita) {
        this.iServicio = iServicio;
        this.iCita = iCita;
    }

    public List<Servicio> consultarTodos() {
        try {
            return iServicio.findAll();
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar los servicios.");
        }
    }

    public Servicio consultarPorId(Integer codservicio) {
        try {
            return iServicio.findById(codservicio)
                    .orElseThrow(() -> new NoEncontradoException("Servicio no registrado."));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar el servicio.");
        }
    }

    public List<Servicio> consultarPorNombre(String nombre) {
        try {
            return iServicio.findByNomservicioContainingIgnoreCase(nombre == null ? "" : nombre.trim());
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al buscar servicios.");
        }
    }

    public Servicio adicionar(Servicio servicio) {
        try {
            validar(servicio);
            verificarNombreUnico(servicio.getNomservicio(), null);
            servicio.setCodservicio(null); // el codigo lo genera la base de datos
            return iServicio.save(servicio);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al adicionar el servicio.");
        }
    }

    public Servicio modificar(Integer codservicio, Servicio datos) {
        try {
            Servicio servicio = consultarPorId(codservicio);
            validar(datos);
            verificarNombreUnico(datos.getNomservicio(), codservicio);
            servicio.setNomservicio(datos.getNomservicio());
            servicio.setPrecio(datos.getPrecio());
            servicio.setDuracion(datos.getDuracion());
            servicio.setActivo(datos.getActivo());
            return iServicio.save(servicio);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al modificar el servicio.");
        }
    }

    public void eliminar(Integer codservicio) {
        try {
            Servicio servicio = consultarPorId(codservicio);
            // Integridad referencial: no se elimina un servicio que tenga citas
            if (iCita.existsByServicio_Codservicio(codservicio)) {
                throw new ReglaNegocioException(
                        "No se puede eliminar el servicio porque tiene citas registradas.");
            }
            iServicio.delete(servicio);
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al eliminar el servicio.");
        }
    }

    private void validar(Servicio servicio) {
        if (servicio == null) {
            throw new ReglaNegocioException("Debe enviar los datos del servicio.");
        }
        servicio.setNomservicio(Validador.texto(servicio.getNomservicio(), "nombre", 3, 60));
        servicio.setPrecio(Validador.entero(servicio.getPrecio(), "precio", 1000, 500000));
        servicio.setDuracion(Validador.entero(servicio.getDuracion(), "duracion", 10, 240));
        servicio.setActivo(Validador.logico(servicio.getActivo(), "activo"));
    }

    // No puede haber dos servicios con el mismo nombre
    private void verificarNombreUnico(String nombre, Integer codservicioActual) {
        Optional<Servicio> existente = iServicio.findByNomservicioIgnoreCase(nombre);
        if (existente.isPresent() && !existente.get().getCodservicio().equals(codservicioActual)) {
            throw new ReglaNegocioException("Ya existe un servicio con ese nombre.");
        }
    }
}

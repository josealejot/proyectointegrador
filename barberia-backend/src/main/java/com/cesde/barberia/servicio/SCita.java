package com.cesde.barberia.servicio;

import com.cesde.barberia.dto.CitaDTO;
import com.cesde.barberia.dto.CitaRegistroDTO;
import com.cesde.barberia.dto.ResumenBarberoDTO;
import com.cesde.barberia.excepcion.NoEncontradoException;
import com.cesde.barberia.excepcion.ReglaNegocioException;
import com.cesde.barberia.mapper.ICitaMapper;
import com.cesde.barberia.mapper.IResumenBarberoMapper;
import com.cesde.barberia.modelo.Barbero;
import com.cesde.barberia.modelo.Catalogos;
import com.cesde.barberia.modelo.Cita;
import com.cesde.barberia.modelo.Cliente;
import com.cesde.barberia.modelo.Servicio;
import com.cesde.barberia.repositorio.IBarbero;
import com.cesde.barberia.repositorio.ICita;
import com.cesde.barberia.repositorio.ICliente;
import com.cesde.barberia.repositorio.IServicio;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class SCita {

    // Horario de atencion de la barberia
    private static final LocalTime HORA_APERTURA = LocalTime.of(8, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(20, 0);

    private final ICita iCita;
    private final ICliente iCliente;
    private final IBarbero iBarbero;
    private final IServicio iServicio;
    private final ICitaMapper iCitaMapper;
    private final IResumenBarberoMapper iResumenBarberoMapper;

    public SCita(ICita iCita, ICliente iCliente, IBarbero iBarbero, IServicio iServicio,
                 ICitaMapper iCitaMapper, IResumenBarberoMapper iResumenBarberoMapper) {
        this.iCita = iCita;
        this.iCliente = iCliente;
        this.iBarbero = iBarbero;
        this.iServicio = iServicio;
        this.iCitaMapper = iCitaMapper;
        this.iResumenBarberoMapper = iResumenBarberoMapper;
    }

    @Transactional(readOnly = true)
    public List<CitaDTO> consultarTodas() {
        try {
            return iCitaMapper.toDTOList(iCita.findAllByOrderByFechaDescHoraDesc());
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar las citas.");
        }
    }

    @Transactional(readOnly = true)
    public CitaDTO consultarPorId(Integer codcita) {
        try {
            return iCitaMapper.toDTO(buscar(codcita));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar la cita.");
        }
    }

    // Filtro por fecha, barbero y estado. Los tres son opcionales.
    @Transactional(readOnly = true)
    public List<CitaDTO> filtrar(LocalDate fecha, String idbarbero, String estado) {
        try {
            String barbero = (idbarbero == null || idbarbero.isBlank()) ? null : idbarbero.trim();
            String estadoCita = (estado == null || estado.isBlank()) ? null : estado.trim();
            return iCitaMapper.toDTOList(iCita.filtrar(fecha, barbero, estadoCita));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al filtrar las citas.");
        }
    }

    @Transactional(readOnly = true)
    public List<ResumenBarberoDTO> resumenPorBarbero() {
        try {
            return iResumenBarberoMapper.toDTOList(iCita.resumenPorBarbero());
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al consultar el resumen.");
        }
    }

    @Transactional
    public CitaDTO adicionar(CitaRegistroDTO datos) {
        try {
            Cita cita = new Cita();
            llenar(cita, datos, true);
            return iCitaMapper.toDTO(iCita.save(cita));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al adicionar la cita.");
        }
    }

    @Transactional
    public CitaDTO modificar(Integer codcita, CitaRegistroDTO datos) {
        try {
            Cita cita = buscar(codcita);
            llenar(cita, datos, false);
            return iCitaMapper.toDTO(iCita.save(cita));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al modificar la cita.");
        }
    }

    @Transactional
    public void eliminar(Integer codcita) {
        try {
            iCita.delete(buscar(codcita));
        } catch (DataAccessException e) {
            throw new ReglaNegocioException("Error de base de datos al eliminar la cita.");
        }
    }

    private Cita buscar(Integer codcita) {
        return iCita.findById(codcita)
                .orElseThrow(() -> new NoEncontradoException("Cita no registrada."));
    }

    // Valida los datos recibidos y los pasa a la entidad.
    // Aqui estan las reglas de negocio de la cita.
    private void llenar(Cita cita, CitaRegistroDTO datos, boolean esNueva) {
        if (datos == null) {
            throw new ReglaNegocioException("Debe enviar los datos de la cita.");
        }
        if (datos.fecha() == null) {
            throw new ReglaNegocioException("El campo fecha es obligatorio.");
        }
        if (datos.hora() == null) {
            throw new ReglaNegocioException("El campo hora es obligatorio.");
        }

        String metodopago = Validador.opcion(datos.metodopago(), "metodo de pago", Catalogos.METODOS_PAGO);
        String estado = esNueva && datos.estado() == null
                ? "Pendiente"
                : Validador.opcion(datos.estado(), "estado", Catalogos.ESTADOS_CITA);
        String observacion = Validador.textoOpcional(datos.observacion(), "observacion", 200);

        // Regla: una cita nueva o pendiente no puede quedar en una fecha anterior a hoy
        boolean cambioFecha = !datos.fecha().equals(cita.getFecha());
        if ((esNueva || cambioFecha || estado.equals("Pendiente"))
                && datos.fecha().isBefore(LocalDate.now())) {
            throw new ReglaNegocioException("La fecha de la cita no puede ser anterior a hoy.");
        }

        // Regla: la hora debe estar dentro del horario de atencion
        if (datos.hora().isBefore(HORA_APERTURA) || datos.hora().isAfter(HORA_CIERRE)) {
            throw new ReglaNegocioException("La hora debe estar entre las 08:00 y las 20:00.");
        }

        // Regla: el cliente, el barbero y el servicio deben existir antes de guardar la cita
        Cliente cliente = iCliente.findById(textoLlave(datos.idcliente(), "cliente"))
                .orElseThrow(() -> new ReglaNegocioException("El cliente seleccionado no existe."));
        Barbero barbero = iBarbero.findById(textoLlave(datos.idbarbero(), "barbero"))
                .orElseThrow(() -> new ReglaNegocioException("El barbero seleccionado no existe."));
        if (datos.codservicio() == null) {
            throw new ReglaNegocioException("El campo servicio es obligatorio.");
        }
        Servicio servicio = iServicio.findById(datos.codservicio())
                .orElseThrow(() -> new ReglaNegocioException("El servicio seleccionado no existe."));

        // Regla: no se agenda una cita nueva con registros inactivos
        if (esNueva) {
            if (!cliente.getActivo()) {
                throw new ReglaNegocioException("El cliente seleccionado esta inactivo.");
            }
            if (!barbero.getActivo()) {
                throw new ReglaNegocioException("El barbero seleccionado esta inactivo.");
            }
            if (!servicio.getActivo()) {
                throw new ReglaNegocioException("El servicio seleccionado esta inactivo.");
            }
        }

        // Regla: un barbero no puede tener dos citas a la misma fecha y hora
        if (!estado.equals("Cancelada")
                && iCita.contarCruces(barbero.getIdbarbero(), datos.fecha(), datos.hora(), cita.getCodcita()) > 0) {
            throw new ReglaNegocioException("El barbero ya tiene una cita en esa fecha y hora.");
        }

        cita.setFecha(datos.fecha());
        cita.setHora(datos.hora());
        cita.setCliente(cliente);
        cita.setBarbero(barbero);
        cita.setServicio(servicio);
        cita.setMetodopago(metodopago);
        cita.setEstado(estado);
        cita.setObservacion(observacion);
    }

    private String textoLlave(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio.");
        }
        return valor.trim();
    }
}

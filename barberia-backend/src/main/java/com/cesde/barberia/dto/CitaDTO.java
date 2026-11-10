package com.cesde.barberia.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Lo que el backend responde al consultar citas: incluye los nombres
// del cliente, el barbero y los servicios para que el front no tenga que buscarlos.
public record CitaDTO(
        Integer codcita,
        LocalDate fecha,
        LocalTime hora,
        String idcliente,
        String nomcliente,
        String idbarbero,
        String nombarbero,
        Integer codservicio,
        List<Integer> codservicios,
        String nomservicio,
        Integer precio,
        Integer duracion,
        String metodopago,
        String estado,
        String observacion) {
}

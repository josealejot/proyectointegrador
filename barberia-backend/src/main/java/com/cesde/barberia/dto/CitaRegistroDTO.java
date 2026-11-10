package com.cesde.barberia.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Lo que el front envia para crear o modificar una cita:
// las llaves del cliente, el barbero y uno o varios servicios.
public record CitaRegistroDTO(
        LocalDate fecha,
        LocalTime hora,
        String idcliente,
        String idbarbero,
        Integer codservicio,
        List<Integer> codservicios,
        String metodopago,
        String estado,
        String observacion) {
}

package com.cesde.barberia.dto;

import java.time.LocalDate;
import java.time.LocalTime;

// Lo que el front envia para crear o modificar una cita:
// solo las llaves del cliente, el barbero y el servicio.
public record CitaRegistroDTO(
        LocalDate fecha,
        LocalTime hora,
        String idcliente,
        String idbarbero,
        Integer codservicio,
        String metodopago,
        String estado,
        String observacion) {
}

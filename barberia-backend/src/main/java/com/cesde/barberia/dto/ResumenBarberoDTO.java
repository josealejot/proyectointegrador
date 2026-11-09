package com.cesde.barberia.dto;

import java.math.BigDecimal;

// Resultado de la consulta nativa de ingresos por barbero
public record ResumenBarberoDTO(
        String idbarbero,
        String nombrebarbero,
        Long citas,
        BigDecimal ingresos) {
}

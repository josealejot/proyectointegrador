package com.cesde.barberia.modelo;

import java.util.List;

// Listas de opciones conocidas. El front las muestra como listas desplegables
// o botones de radio y el Servicio verifica que el valor recibido este en la lista.
public final class Catalogos {

    public static final List<String> ESPECIALIDADES =
            List.of("Corte clasico", "Barba", "Degradado", "Colorimetria", "Infantil");

    public static final List<String> METODOS_PAGO =
            List.of("Efectivo", "Tarjeta", "Transferencia");

    public static final List<String> ESTADOS_CITA =
            List.of("Pendiente", "Atendida", "Incumplida", "Cancelada");

    public static final List<String> ROLES =
            List.of("Administrador", "Recepcionista", "Barbero", "Cliente");

    private Catalogos() {
    }
}

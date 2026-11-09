package com.cesde.barberia.excepcion;

// Se lanza desde la capa de Servicio cuando un dato no cumple una regla del negocio.
// El Controlador la atrapa y responde 400 con el mensaje.
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}

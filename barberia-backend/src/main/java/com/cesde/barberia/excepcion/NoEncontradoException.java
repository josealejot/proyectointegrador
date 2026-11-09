package com.cesde.barberia.excepcion;

// Se lanza cuando se busca un registro que no existe.
// El Controlador la atrapa y responde 404 con el mensaje.
public class NoEncontradoException extends RuntimeException {

    public NoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

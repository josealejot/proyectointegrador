package com.cesde.barberia.servicio;

import com.cesde.barberia.excepcion.ReglaNegocioException;

import java.util.List;

// Validaciones de tipo y extension que comparten todos los servicios.
// Cada metodo devuelve el valor limpio o lanza ReglaNegocioException.
public final class Validador {

    private static final String PATRON_CORREO = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";

    private Validador() {
    }

    // Texto obligatorio con longitud minima y maxima
    public static String texto(String valor, String campo, int minimo, int maximo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio.");
        }
        String limpio = valor.trim();
        if (limpio.length() < minimo || limpio.length() > maximo) {
            String rango = minimo == maximo ? String.valueOf(minimo) : "entre " + minimo + " y " + maximo;
            throw new ReglaNegocioException("El campo " + campo + " debe tener " + rango + " caracteres.");
        }
        return limpio;
    }

    // Texto opcional: si no llega queda vacio, pero no puede pasar del maximo
    public static String textoOpcional(String valor, String campo, int maximo) {
        if (valor == null) {
            return "";
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new ReglaNegocioException(
                    "El campo " + campo + " no puede tener mas de " + maximo + " caracteres.");
        }
        return limpio;
    }

    // Texto obligatorio formado solo por numeros (cedulas y telefonos)
    public static String digitos(String valor, String campo, int minimo, int maximo) {
        String limpio = texto(valor, campo, minimo, maximo);
        if (!limpio.matches("\\d+")) {
            throw new ReglaNegocioException("El campo " + campo + " solo puede tener numeros.");
        }
        return limpio;
    }

    public static String correo(String valor) {
        String limpio = texto(valor, "correo", 5, 100).toLowerCase();
        if (!limpio.matches(PATRON_CORREO)) {
            throw new ReglaNegocioException("El correo no tiene un formato valido.");
        }
        return limpio;
    }

    // El valor debe ser una de las opciones conocidas
    public static String opcion(String valor, String campo, List<String> opciones) {
        if (valor == null || !opciones.contains(valor.trim())) {
            throw new ReglaNegocioException(
                    "El campo " + campo + " debe ser una de estas opciones: " + String.join(", ", opciones) + ".");
        }
        return valor.trim();
    }

    public static Integer entero(Integer valor, String campo, int minimo, int maximo) {
        if (valor == null) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio.");
        }
        if (valor < minimo || valor > maximo) {
            throw new ReglaNegocioException(
                    "El campo " + campo + " debe estar entre " + minimo + " y " + maximo + ".");
        }
        return valor;
    }

    public static Boolean logico(Boolean valor, String campo) {
        if (valor == null) {
            throw new ReglaNegocioException("El campo " + campo + " es obligatorio (true o false).");
        }
        return valor;
    }
}

package com.cesde.barberia.dto;

// Datos que llegan para registrar o modificar un usuario.
// En el registro publico se ignoran rol y activo.
public record UsuarioRegistroDTO(
        String nombre,
        String correo,
        String contrasena,
        String rol,
        Boolean activo,
        String identificacion,
        String telefono,
        String especialidad) {

    public UsuarioRegistroDTO(String nombre, String correo, String contrasena, String rol, Boolean activo) {
        this(nombre, correo, contrasena, rol, activo, null, null, null);
    }
}

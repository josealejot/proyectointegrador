package com.cesde.barberia.dto;

// Datos del usuario que se pueden mostrar. No incluye la contrasena.
public record UsuarioDTO(
        Integer idusuario,
        String nombre,
        String correo,
        String rol,
        Boolean activo,
        String identificacion) {
}

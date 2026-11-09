package com.cesde.barberia.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Usuarios que pueden iniciar sesion en el sistema.
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idusuario;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 100, unique = true)
    private String correo;

    // Se guarda cifrada con BCrypt, nunca en texto plano.
    @Column(nullable = false, length = 100)
    private String contrasena;

    @Column(nullable = false, length = 20)
    private String rol;

    @Column(nullable = false)
    private Boolean activo;

    public Usuario() {
    }

    public Integer getIdusuario() { return idusuario; }
    public void setIdusuario(Integer idusuario) { this.idusuario = idusuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}

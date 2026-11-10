package com.cesde.barberia.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Identificador unico del usuario", example = "1")
    private Integer idusuario;

    @Column(nullable = false, length = 80)
    @Schema(description = "Nombre completo del usuario", example = "Andres Zapata")
    private String nombre;

    @Column(nullable = false, length = 100, unique = true)
    @Schema(description = "Correo electronico de acceso", example = "andres@barberia.com")
    private String correo;

    // Se guarda cifrada con BCrypt, nunca en texto plano.
    @Column(nullable = false, length = 100)
    @Schema(description = "Contrasena cifrada con BCrypt", example = "$2a$10$...")
    private String contrasena;

    @Column(nullable = false, length = 20)
    @Schema(description = "Rol del usuario en el sistema", example = "Barbero")
    private String rol;

    @Column(nullable = false)
    @Schema(description = "Indica si la cuenta de usuario esta activa o no", example = "true")
    private Boolean activo;

    // Vinculo opcional con la cedula del barbero o del cliente cuando el rol es Barbero o Cliente
    @Column(length = 10)
    @Schema(description = "Cedula asociada al perfil de cliente o barbero", example = "71345678")
    private String identificacion;

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

    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
}

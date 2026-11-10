package com.cesde.barberia.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "barbero")
public class Barbero {

    @Id
    @Column(length = 10)
    @Schema(description = "Identificacion (cedula) del barbero", example = "71345678")
    private String idbarbero;

    @Column(nullable = false, length = 80)
    @Schema(description = "Nombre completo del barbero", example = "Andres Zapata")
    private String nombarbero;

    @Column(nullable = false, length = 10)
    @Schema(description = "Telefono o celular del barbero (10 digitos)", example = "3004567890")
    private String telbarbero;

    @Column(nullable = false, length = 30)
    @Schema(description = "Especialidad principal del barbero", example = "Degradado")
    private String especialidad;

    @Column(nullable = false)
    @Schema(description = "Indica si el barbero esta activo o no", example = "true")
    private Boolean activo;

    // Correo y contrasena opcionales: si se envian al crear/editar un barbero, se crea su cuenta de acceso
    @jakarta.persistence.Transient
    private String correo;

    @jakarta.persistence.Transient
    private String contrasena;

    public Barbero() {
    }

    public String getIdbarbero() { return idbarbero; }
    public void setIdbarbero(String idbarbero) { this.idbarbero = idbarbero; }

    public String getNombarbero() { return nombarbero; }
    public void setNombarbero(String nombarbero) { this.nombarbero = nombarbero; }

    public String getTelbarbero() { return telbarbero; }
    public void setTelbarbero(String telbarbero) { this.telbarbero = telbarbero; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}

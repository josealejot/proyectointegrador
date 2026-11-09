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
    @Schema(description = "Cedula del barbero", example = "71345678")
    private String idbarbero;

    @Column(nullable = false, length = 80)
    private String nombarbero;

    @Column(nullable = false, length = 10)
    private String telbarbero;

    @Column(nullable = false, length = 30)
    private String especialidad;

    @Column(nullable = false)
    private Boolean activo;

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
}

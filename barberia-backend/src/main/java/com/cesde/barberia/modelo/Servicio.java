package com.cesde.barberia.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codservicio;

    @Column(nullable = false, length = 60, unique = true)
    private String nomservicio;

    // Precio en pesos colombianos
    @Column(nullable = false)
    private Integer precio;

    // Duracion en minutos
    @Column(nullable = false)
    private Integer duracion;

    @Column(nullable = false)
    private Boolean activo;

    public Servicio() {
    }

    public Integer getCodservicio() { return codservicio; }
    public void setCodservicio(Integer codservicio) { this.codservicio = codservicio; }

    public String getNomservicio() { return nomservicio; }
    public void setNomservicio(String nomservicio) { this.nomservicio = nomservicio; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public Integer getDuracion() { return duracion; }
    public void setDuracion(Integer duracion) { this.duracion = duracion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}

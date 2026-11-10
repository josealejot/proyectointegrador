package com.cesde.barberia.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Codigo unico autoincremental del servicio", example = "1")
    private Integer codservicio;

    @Column(nullable = false, length = 60, unique = true)
    @Schema(description = "Nombre del servicio ofrecido en la barberia", example = "Corte y barba")
    private String nomservicio;

    // Precio en pesos colombianos
    @Column(nullable = false)
    @Schema(description = "Precio del servicio en pesos colombianos", example = "38000")
    private Integer precio;

    // Duracion en minutos
    @Column(nullable = false)
    @Schema(description = "Duracion estimada del servicio en minutos", example = "45")
    private Integer duracion;

    @Column(nullable = false)
    @Schema(description = "Indica si el servicio esta activo o no", example = "true")
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

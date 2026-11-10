package com.cesde.barberia.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// Tabla de movimiento: cada cita pertenece a un cliente, un barbero y uno o varios servicios.
@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Codigo unico de la cita", example = "1")
    private Integer codcita;

    @Column(nullable = false)
    @Schema(description = "Fecha programada de la cita", example = "2026-11-15")
    private LocalDate fecha;

    @Column(nullable = false)
    @Schema(description = "Hora programada de la cita (entre 08:00 y 20:00)", example = "10:30")
    private LocalTime hora;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idcliente", referencedColumnName = "idcliente")
    @Schema(description = "Cliente que agenda la cita")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idbarbero", referencedColumnName = "idbarbero")
    @Schema(description = "Barbero asignado a la cita")
    private Barbero barbero;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codservicio", referencedColumnName = "codservicio")
    @Schema(description = "Servicio principal de la cita")
    private Servicio servicio;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cita_servicio",
            joinColumns = @JoinColumn(name = "codcita"),
            inverseJoinColumns = @JoinColumn(name = "codservicio")
    )
    @Schema(description = "Lista de todos los servicios incluidos en la cita")
    private List<Servicio> servicios = new ArrayList<>();

    @Column(nullable = false, length = 20)
    @Schema(description = "Metodo de pago elegido (Efectivo, Tarjeta, Transferencia)", example = "Efectivo")
    private String metodopago;

    @Column(nullable = false, length = 20)
    @Schema(description = "Estado de la cita (Pendiente, Atendida, Incumplida, Cancelada)", example = "Pendiente")
    private String estado;

    @Column(nullable = false, length = 200)
    @Schema(description = "Observaciones adicionales de la cita", example = "Degradado medio con navaja")
    private String observacion;

    public Cita() {
    }

    public Integer getCodcita() { return codcita; }
    public void setCodcita(Integer codcita) { this.codcita = codcita; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Barbero getBarbero() { return barbero; }
    public void setBarbero(Barbero barbero) { this.barbero = barbero; }

    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }

    public List<Servicio> getServicios() { return servicios; }
    public void setServicios(List<Servicio> servicios) { this.servicios = servicios; }

    public String getMetodopago() { return metodopago; }
    public void setMetodopago(String metodopago) { this.metodopago = metodopago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}

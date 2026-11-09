package com.cesde.barberia.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;

// Tabla de movimiento: cada cita pertenece a un cliente, un barbero y un servicio.
@Entity
@Table(name = "cita")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codcita;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private LocalTime hora;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idcliente", referencedColumnName = "idcliente")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idbarbero", referencedColumnName = "idbarbero")
    private Barbero barbero;

    @ManyToOne(optional = false)
    @JoinColumn(name = "codservicio", referencedColumnName = "codservicio")
    private Servicio servicio;

    @Column(nullable = false, length = 20)
    private String metodopago;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(nullable = false, length = 200)
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

    public String getMetodopago() { return metodopago; }
    public void setMetodopago(String metodopago) { this.metodopago = metodopago; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }
}

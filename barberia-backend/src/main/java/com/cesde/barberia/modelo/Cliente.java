package com.cesde.barberia.modelo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

    @Id
    @Column(length = 10)
    @Schema(description = "Cedula del cliente", example = "1017123456")
    private String idcliente;

    @Column(nullable = false, length = 80)
    private String nomcliente;

    @Column(nullable = false, length = 10)
    private String telcliente;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false)
    private Boolean activo;

    public Cliente() {
    }

    public String getIdcliente() { return idcliente; }
    public void setIdcliente(String idcliente) { this.idcliente = idcliente; }

    public String getNomcliente() { return nomcliente; }
    public void setNomcliente(String nomcliente) { this.nomcliente = nomcliente; }

    public String getTelcliente() { return telcliente; }
    public void setTelcliente(String telcliente) { this.telcliente = telcliente; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}

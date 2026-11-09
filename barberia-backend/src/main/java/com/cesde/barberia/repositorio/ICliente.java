package com.cesde.barberia.repositorio;

import com.cesde.barberia.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICliente extends JpaRepository<Cliente, String> {

    // Busqueda por parte del nombre, sin importar mayusculas
    List<Cliente> findByNomclienteContainingIgnoreCase(String nomcliente);
}

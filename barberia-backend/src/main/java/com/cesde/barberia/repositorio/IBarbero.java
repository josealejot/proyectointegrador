package com.cesde.barberia.repositorio;

import com.cesde.barberia.modelo.Barbero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBarbero extends JpaRepository<Barbero, String> {

    List<Barbero> findByNombarberoContainingIgnoreCase(String nombarbero);
}

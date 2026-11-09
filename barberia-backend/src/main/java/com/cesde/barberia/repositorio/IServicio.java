package com.cesde.barberia.repositorio;

import com.cesde.barberia.modelo.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IServicio extends JpaRepository<Servicio, Integer> {

    List<Servicio> findByNomservicioContainingIgnoreCase(String nomservicio);

    // Sirve para no permitir dos servicios con el mismo nombre
    Optional<Servicio> findByNomservicioIgnoreCase(String nomservicio);
}

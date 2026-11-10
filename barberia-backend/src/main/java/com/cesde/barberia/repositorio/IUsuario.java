package com.cesde.barberia.repositorio;

import com.cesde.barberia.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUsuario extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    Optional<Usuario> findByIdentificacion(String identificacion);

    long countByRolAndActivoTrue(String rol);
}

package com.cesde.barberia.repositorio;

import com.cesde.barberia.modelo.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ICita extends JpaRepository<Cita, Integer> {

    List<Cita> findAllByOrderByFechaDescHoraDesc();

    // Integridad referencial: saber si un cliente, barbero o servicio tiene citas
    boolean existsByCliente_Idcliente(String idcliente);

    boolean existsByBarbero_Idbarbero(String idbarbero);

    boolean existsByServicio_Codservicio(Integer codservicio);

    boolean existsByServicios_Codservicio(Integer codservicio);

    // Cuenta las citas no canceladas del mismo barbero en la misma fecha y hora.
    // El parametro codcita permite ignorar la cita que se esta modificando.
    @Query("""
            select count(c) from Cita c
            where c.barbero.idbarbero = :idbarbero
              and c.fecha = :fecha
              and c.hora = :hora
              and c.estado not in ('Cancelada', 'Incumplida')
              and (:codcita is null or c.codcita <> :codcita)""")
    long contarCruces(@Param("idbarbero") String idbarbero,
                      @Param("fecha") LocalDate fecha,
                      @Param("hora") LocalTime hora,
                      @Param("codcita") Integer codcita);

    // Filtro de citas: cada parametro es opcional
    @Query("""
            select c from Cita c
            where (:fecha is null or c.fecha = :fecha)
              and (:idbarbero is null or c.barbero.idbarbero = :idbarbero)
              and (:estado is null or c.estado = :estado)
            order by c.fecha desc, c.hora desc""")
    List<Cita> filtrar(@Param("fecha") LocalDate fecha,
                       @Param("idbarbero") String idbarbero,
                       @Param("estado") String estado);

    // Consulta nativa: citas atendidas e ingresos por cada barbero (suma todos los servicios de la cita)
    @Query(value = """
            SELECT
              B.idbarbero AS idbarbero,
              B.nombarbero AS nombarbero,
              COUNT(DISTINCT C.codcita) AS citas,
              COALESCE(SUM(COALESCE(CS_SUM.total_precio, S.precio)), 0) AS ingresos
            FROM barbero B
              INNER JOIN cita C ON B.idbarbero = C.idbarbero
              INNER JOIN servicio S ON C.codservicio = S.codservicio
              LEFT JOIN (
                SELECT CS.codcita, SUM(S2.precio) AS total_precio
                FROM cita_servicio CS
                INNER JOIN servicio S2 ON CS.codservicio = S2.codservicio
                GROUP BY CS.codcita
              ) CS_SUM ON C.codcita = CS_SUM.codcita
            WHERE C.estado = 'Atendida'
            GROUP BY B.idbarbero, B.nombarbero
            ORDER BY ingresos DESC""", nativeQuery = true)
    List<IResumenBarberoProjection> resumenPorBarbero();

    // Proyeccion: los nombres de los metodos coinciden con los alias de la consulta
    interface IResumenBarberoProjection {
        String getIdbarbero();
        String getNombarbero();
        Long getCitas();
        BigDecimal getIngresos();
    }
}

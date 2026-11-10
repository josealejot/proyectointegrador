package com.cesde.barberia.mapper;

import com.cesde.barberia.dto.CitaDTO;
import com.cesde.barberia.modelo.Cita;
import com.cesde.barberia.modelo.Servicio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

// Convierte la entidad Cita (con sus relaciones) en la DTO plana que recibe el front
@Mapper(componentModel = "spring")
public interface ICitaMapper {

    @Mapping(source = "cliente.idcliente", target = "idcliente")
    @Mapping(source = "cliente.nomcliente", target = "nomcliente")
    @Mapping(source = "barbero.idbarbero", target = "idbarbero")
    @Mapping(source = "barbero.nombarbero", target = "nombarbero")
    @Mapping(source = "servicio.codservicio", target = "codservicio")
    @Mapping(target = "codservicios", expression = "java(mapCodservicios(cita))")
    @Mapping(target = "nomservicio", expression = "java(mapNomservicio(cita))")
    @Mapping(target = "precio", expression = "java(mapPrecio(cita))")
    @Mapping(target = "duracion", expression = "java(mapDuracion(cita))")
    CitaDTO toDTO(Cita cita);

    List<CitaDTO> toDTOList(List<Cita> citas);

    default List<Servicio> listaEfectiva(Cita cita) {
        if (cita.getServicios() != null && !cita.getServicios().isEmpty()) {
            return cita.getServicios();
        }
        return cita.getServicio() != null ? List.of(cita.getServicio()) : List.of();
    }

    default List<Integer> mapCodservicios(Cita cita) {
        return listaEfectiva(cita).stream()
                .map(Servicio::getCodservicio)
                .collect(Collectors.toList());
    }

    default String mapNomservicio(Cita cita) {
        return listaEfectiva(cita).stream()
                .map(Servicio::getNomservicio)
                .collect(Collectors.joining(" + "));
    }

    default Integer mapPrecio(Cita cita) {
        return listaEfectiva(cita).stream()
                .mapToInt(s -> s.getPrecio() != null ? s.getPrecio() : 0)
                .sum();
    }

    default Integer mapDuracion(Cita cita) {
        return listaEfectiva(cita).stream()
                .mapToInt(s -> s.getDuracion() != null ? s.getDuracion() : 0)
                .sum();
    }
}

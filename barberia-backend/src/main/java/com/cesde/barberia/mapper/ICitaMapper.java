package com.cesde.barberia.mapper;

import com.cesde.barberia.dto.CitaDTO;
import com.cesde.barberia.modelo.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// Convierte la entidad Cita (con sus relaciones) en la DTO plana que recibe el front
@Mapper(componentModel = "spring")
public interface ICitaMapper {

    @Mapping(source = "cliente.idcliente", target = "idcliente")
    @Mapping(source = "cliente.nomcliente", target = "nomcliente")
    @Mapping(source = "barbero.idbarbero", target = "idbarbero")
    @Mapping(source = "barbero.nombarbero", target = "nombarbero")
    @Mapping(source = "servicio.codservicio", target = "codservicio")
    @Mapping(source = "servicio.nomservicio", target = "nomservicio")
    @Mapping(source = "servicio.precio", target = "precio")
    @Mapping(source = "servicio.duracion", target = "duracion")
    CitaDTO toDTO(Cita cita);

    List<CitaDTO> toDTOList(List<Cita> citas);
}

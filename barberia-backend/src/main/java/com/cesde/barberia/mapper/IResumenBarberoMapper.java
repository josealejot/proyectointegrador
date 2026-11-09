package com.cesde.barberia.mapper;

import com.cesde.barberia.dto.ResumenBarberoDTO;
import com.cesde.barberia.repositorio.ICita.IResumenBarberoProjection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

// Convierte la proyeccion de la consulta nativa en la DTO
@Mapper(componentModel = "spring")
public interface IResumenBarberoMapper {

    @Mapping(source = "nombarbero", target = "nombrebarbero")
    ResumenBarberoDTO toDTO(IResumenBarberoProjection proyeccion);

    List<ResumenBarberoDTO> toDTOList(List<IResumenBarberoProjection> proyecciones);
}

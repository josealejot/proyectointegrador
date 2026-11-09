package com.cesde.barberia.mapper;

import com.cesde.barberia.dto.UsuarioDTO;
import com.cesde.barberia.modelo.Usuario;
import org.mapstruct.Mapper;

import java.util.List;

// Convierte la entidad Usuario en una DTO sin la contrasena
@Mapper(componentModel = "spring")
public interface IUsuarioMapper {

    UsuarioDTO toDTO(Usuario usuario);

    List<UsuarioDTO> toDTOList(List<Usuario> usuarios);
}

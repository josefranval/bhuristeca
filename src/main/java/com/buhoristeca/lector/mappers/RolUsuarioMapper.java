package com.buhoristeca.lector.mappers;

import com.buhoristeca.lector.dtos.RolUsuarioRequestDTO;
import com.buhoristeca.lector.dtos.RolUsuarioResponseDTO;
import com.buhoristeca.lector.model.RolUsuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolUsuarioMapper {
    RolUsuarioResponseDTO rolUsuarioToRolUsuarioResponseDTO(RolUsuario rolUsuario);

    @Mapping(target = "idRolUsuario", ignore = true)
    RolUsuario rolUsuarioRequestDTOToRolUsuario(RolUsuarioRequestDTO rolUsuarioRequestDTO);
}

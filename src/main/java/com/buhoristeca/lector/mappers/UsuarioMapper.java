package com.buhoristeca.lector.mappers;


import com.buhoristeca.lector.dtos.UsuarioRequestDTO;
import com.buhoristeca.lector.dtos.UsuarioResponseDTO;
import com.buhoristeca.lector.dtos.UsuarioSummaryResponseDTO;
import com.buhoristeca.lector.model.RolUsuario;
import com.buhoristeca.lector.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = RolUsuarioMapper.class)
public interface UsuarioMapper {

    @Mappings({
            @Mapping(source = "dto.nombre", target = "nombre"),
            @Mapping(source = "dto.apellido", target = "apellido"),
            @Mapping(source = "dto.dni", target = "dni"),
            @Mapping(source = "dto.fechaNacimiento", target = "fechaNacimiento"),
            @Mapping(source = "dto.direccion", target = "direccion"),
            @Mapping(source = "dto.correoElectronico", target = "correoElectronico"),
            @Mapping(target = "idUsuario", ignore = true),
            @Mapping(target = "rolUsuario", source = "rolUsuarioEntity"),
            @Mapping(target = "activo", ignore = true),
            @Mapping(target = "prestamos", ignore = true)
    })
    Usuario usuarioRequestDTOToUsuario(UsuarioRequestDTO dto, RolUsuario rolUsuarioEntity);

    @Mappings({
            @Mapping(source = "rolUsuario", target = "rolUsuario")
    })
    UsuarioResponseDTO usuarioToUsuarioResponseDTO(Usuario usuario);

    UsuarioSummaryResponseDTO usuarioToUsuarioSummaryResponseDTO(Usuario usuario);
}

package com.buhoristeca.lector.mappers;


import com.buhoristeca.lector.dtos.GeneroRequestDTO;
import com.buhoristeca.lector.dtos.GeneroResponseDTO;
import com.buhoristeca.lector.model.Genero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface GeneroMapper {
    GeneroResponseDTO generoToGeneroResponseDTO(Genero genero);
    List<GeneroResponseDTO> generosSetToGeneroResponseDTOList(Set<Genero> generos);

    @Mapping(target = "idGenero", ignore = true)
    @Mapping(target = "libros", ignore = true)
    Genero generoRequestDTOToGenero(GeneroRequestDTO generoRequestDTO);
}
package com.buhoristeca.lector.mappers;

import com.buhoristeca.lector.dtos.IdiomaRequestDTO;
import com.buhoristeca.lector.dtos.IdiomaResponseDTO;
import com.buhoristeca.lector.model.Idioma;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IdiomaMapper {
    IdiomaResponseDTO idiomaToIdiomaResponseDTO(Idioma idioma);

    @Mapping(target = "idIdioma", ignore = true)
    Idioma idiomaRequestDTOToIdioma(IdiomaRequestDTO idiomaRequestDTO);
}
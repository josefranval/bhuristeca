package com.buhoristeca.lector.mappers;

import com.buhoristeca.lector.dtos.EditorialRequestDTO;
import com.buhoristeca.lector.dtos.EditorialResponseDTO;
import com.buhoristeca.lector.model.Editorial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EditorialMapper {
    EditorialResponseDTO editorialToEditorialResponseDTO(Editorial editorial);

    @Mapping(target = "idEditorial", ignore = true)
    Editorial editorialRequestDTOToEditorial(EditorialRequestDTO editorialRequestDTO);
}
package com.buhoristeca.lector.mappers;

import com.buhoristeca.lector.dtos.ReportePopularidadGeneros200ResponseInnerDTO;
import com.buhoristeca.lector.dtos.GeneroResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = GeneroMapper.class)
public interface InformeMapper {

    @Mapping(source = "generoDTO", target = "genero")
    @Mapping(source = "totalPrestamos", target = "totalPrestamos")
    ReportePopularidadGeneros200ResponseInnerDTO toReportePopularidadDTO(GeneroResponseDTO generoDTO, Integer totalPrestamos);
}
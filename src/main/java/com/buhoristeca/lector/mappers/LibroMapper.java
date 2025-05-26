package com.buhoristeca.lector.mappers;


import com.buhoristeca.lector.dtos.LibroRequestDTO;
import com.buhoristeca.lector.dtos.LibroResponseDTO;
import com.buhoristeca.lector.dtos.LibroSummaryResponseDTO;
import com.buhoristeca.lector.model.Editorial;
import com.buhoristeca.lector.model.Genero;
import com.buhoristeca.lector.model.Idioma;
import com.buhoristeca.lector.model.Libro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {EditorialMapper.class, IdiomaMapper.class, GeneroMapper.class, CommonMapperUtils.class})
public interface LibroMapper {

    @Mappings({
            @Mapping(target = "idLibro", ignore = true),
            @Mapping(target = "editorial", source = "editorialEntity"),
            @Mapping(target = "idioma", source = "idiomaEntity"),
            @Mapping(target = "generos", source = "generosSet"),
            @Mapping(target = "activo", ignore = true),
            @Mapping(target = "prestamos", ignore = true),
            @Mapping(source = "dto.titulo", target = "titulo"), // Mapeo desde DTO
            @Mapping(source = "dto.autor", target = "autor"),
            @Mapping(source = "dto.descripcion", target = "descripcion"),
            @Mapping(source = "dto.fechaPublicacion", target = "fechaPublicacion"),
            @Mapping(source = "dto.isbn", target = "isbn"),
            @Mapping(source = "dto.cantidadEjemplares", target = "cantidadEjemplares"),
            @Mapping(source = "dto.rutaPdf", target = "rutaPdf", qualifiedByName = "unwrapJsonNullableString")
    })
    Libro libroRequestDTOToLibro(LibroRequestDTO dto, Editorial editorialEntity, Idioma idiomaEntity, Set<Genero> generosSet);

    @Mappings({
            @Mapping(source = "editorial", target = "editorial"),
            @Mapping(source = "idioma", target = "idioma"),
            @Mapping(source = "generos", target = "generos"),
            @Mapping(target = "tieneDigital", expression = "java(org.springframework.util.StringUtils.hasText(libro.getRutaPdf()))"),
            @Mapping(source = "rutaPdf", target = "rutaPdf", qualifiedByName = "wrapJsonNullableString")

    })
    LibroResponseDTO libroToLibroResponseDTO(Libro libro);

    LibroSummaryResponseDTO libroToLibroSummaryResponseDTO(Libro libro);
}
package com.buhoristeca.lector.mappers;


import com.buhoristeca.lector.dtos.PrestamoRequestDTO;
import com.buhoristeca.lector.dtos.PrestamoResponseDTO;
import com.buhoristeca.lector.model.Libro;
import com.buhoristeca.lector.model.Prestamo;
import com.buhoristeca.lector.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {LibroMapper.class, UsuarioMapper.class, CommonMapperUtils.class})
public interface PrestamoMapper {

    @Mappings({
            @Mapping(target = "idPrestamo", ignore = true),
            @Mapping(target = "libro", source = "libroEntity"),
            @Mapping(target = "usuario", source = "usuarioEntity"),
            @Mapping(target = "fechaPrestamo", ignore = true),
            @Mapping(target = "fechaDevolucionEsperada", ignore = true),
            @Mapping(target = "fechaDevolucionReal", ignore = true),
            @Mapping(target = "estadoPrestamo", ignore = true)
    })
    Prestamo prestamoRequestDTOToPrestamo(PrestamoRequestDTO dto, Libro libroEntity, Usuario usuarioEntity);

    @Mappings({
            @Mapping(source = "libro", target = "libro"),
            @Mapping(source = "usuario", target = "usuario"),
            @Mapping(source = "fechaPrestamo", target = "fechaPrestamo", qualifiedByName = "localDateTimeToOffsetDateTime"),
            @Mapping(source = "fechaDevolucionReal", target = "fechaDevolucionReal", qualifiedByName = "wrapJsonNullableLocalDate"),
            @Mapping(source = "estadoPrestamo", target = "estadoPrestamo") // MapStruct mapea EstadoPrestamo (entity) a EstadoPrestamoDTO
    })
    PrestamoResponseDTO prestamoToPrestamoResponseDTO(Prestamo prestamo);
}

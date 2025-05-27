package com.buhoristeca.lector.mappers;

import com.buhoristeca.lector.dtos.FieldErrorDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.validation.FieldError;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FieldErrorMapper {
    @Mapping(source = "objectName", target = "objectName")
    @Mapping(source = "field", target = "field")
    @Mapping(source = "defaultMessage", target = "message")
    FieldErrorDTO springFieldErrorToFieldErrorDTO(FieldError fieldError);
    List<FieldErrorDTO> springFieldErrorsToFieldErrorDTOs(List<FieldError> fieldErrors);
}
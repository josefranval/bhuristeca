package com.buhoristeca.lector.mappers;

import com.buhoristeca.lector.dtos.FieldErrorDTO;
import org.mapstruct.Named;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Collections;

/**
 * Utilidades comunes para los mappers, especialmente para manejar JsonNullable y conversiones de fecha/hora.
 * Anotado con @Component para que Spring lo gestione como un bean y pueda ser inyectado/usado por MapStruct.
 */
@Component
public class CommonMapperUtils {

    @Named("unwrapJsonNullableString")
    public String unwrapJsonNullableString(JsonNullable<String> jsonNullable) {
        return jsonNullable == null || !jsonNullable.isPresent() ? null : jsonNullable.get();
    }

    @Named("wrapJsonNullableString")
    public JsonNullable<String> wrapJsonNullableString(String value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }

    @Named("unwrapJsonNullableInteger")
    public Integer unwrapJsonNullableInteger(JsonNullable<Integer> jsonNullable) {
        return jsonNullable == null || !jsonNullable.isPresent() ? null : jsonNullable.get();
    }

    @Named("wrapJsonNullableInteger")
    public JsonNullable<Integer> wrapJsonNullableInteger(Integer value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }

    @Named("unwrapJsonNullableLocalDate")
    public LocalDate unwrapJsonNullableLocalDate(JsonNullable<LocalDate> jsonNullable) {
        return jsonNullable == null || !jsonNullable.isPresent() ? null : jsonNullable.get();
    }

    @Named("wrapJsonNullableLocalDate")
    public JsonNullable<LocalDate> wrapJsonNullableLocalDate(LocalDate value) {
        return value == null ? JsonNullable.undefined() : JsonNullable.of(value);
    }

    @Named("offsetDateTimeToLocalDateTime")
    public LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }

    @Named("localDateTimeToOffsetDateTime")
    public OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    @Named("unwrapJsonNullableFieldErrorList")
    public List<FieldErrorDTO> unwrapJsonNullableFieldErrorList(JsonNullable<List<FieldErrorDTO>> jsonNullable) {
        return jsonNullable == null || !jsonNullable.isPresent() ? Collections.emptyList() : jsonNullable.get();
    }

    @Named("wrapJsonNullableFieldErrorList")
    public JsonNullable<List<FieldErrorDTO>> wrapJsonNullableFieldErrorList(List<FieldErrorDTO> value) {
        return value == null || value.isEmpty() ? JsonNullable.undefined() : JsonNullable.of(value);
    }
}

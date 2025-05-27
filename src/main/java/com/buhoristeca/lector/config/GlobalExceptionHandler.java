package com.buhoristeca.lector.config;

import com.buhoristeca.lector.dtos.ErrorResponseDTO;
import com.buhoristeca.lector.dtos.FieldErrorDTO;
import com.buhoristeca.lector.exceptions.*;
import com.buhoristeca.lector.mappers.FieldErrorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.openapitools.jackson.nullable.JsonNullable;


import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final FieldErrorMapper fieldErrorMapper;

    @Autowired
    public GlobalExceptionHandler(FieldErrorMapper fieldErrorMapper) {
        this.fieldErrorMapper = fieldErrorMapper;
    }

    private ErrorResponseDTO buildErrorResponse(HttpStatus status, String message, WebRequest request, List<FieldErrorDTO> fieldErrorsList) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO();
        errorResponse.setTimestamp(OffsetDateTime.now(ZoneId.systemDefault())); // Usar OffsetDateTime
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setMessage(message);
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        if (fieldErrorsList != null && !fieldErrorsList.isEmpty()) {
            errorResponse.setFieldErrors(JsonNullable.of(fieldErrorsList));
        } else {
            errorResponse.setFieldErrors(JsonNullable.undefined());
            }
        return errorResponse;
    }

    private ErrorResponseDTO buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        return buildErrorResponse(status, message, request, null);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(BadRequestException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PrestamoValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handlePrestamoValidationException(PrestamoValidationException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ErrorResponseDTO> handleStockInsuficienteException(StockInsuficienteException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileStorageException(FileStorageException ex, WebRequest request) {
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<FieldErrorDTO> errors = fieldErrorMapper.springFieldErrorsToFieldErrorDTOs(ex.getBindingResult().getFieldErrors());

        String errorMessage = "Error de validación. Por favor, verifique los campos.";
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, request, errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex, WebRequest request) {
        ex.printStackTrace();
        ErrorResponseDTO errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor: " + ex.getClass().getSimpleName() + " - " + ex.getMessage(), request);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

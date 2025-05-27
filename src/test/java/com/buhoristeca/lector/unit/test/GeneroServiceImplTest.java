package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.dtos.GeneroRequestDTO;
import com.buhoristeca.lector.dtos.GeneroResponseDTO;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.mappers.GeneroMapper;
import com.buhoristeca.lector.model.Genero;
import com.buhoristeca.lector.repositories.GeneroRepository;
import com.buhoristeca.lector.service.impl.GeneroServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneroServiceImplTest {

    @Mock
    private GeneroRepository generoRepository;

    @Mock
    private GeneroMapper generoMapper;

    @InjectMocks
    private GeneroServiceImpl generoService;

    private Genero genero;
    private GeneroRequestDTO generoRequestDTO;
    private GeneroResponseDTO generoResponseDTO;

    @BeforeEach
    void setUp() {
        generoRequestDTO = new GeneroRequestDTO("Fantasía Test");

        genero = new Genero();
        genero.setIdGenero(1L);
        genero.setNombreGenero("Fantasía Test");
        generoResponseDTO = new GeneroResponseDTO();
        generoResponseDTO.setIdGenero(1L);
        generoResponseDTO.setNombreGenero("Fantasía Test");
    }

    @Test
    @DisplayName("Crear Genero Exitosamente")
    void crearGenero_cuandoNoExiste_deberiaRetornarGeneroDTO() {
        // Arrange
        when(generoRepository.findByNombreGenero(generoRequestDTO.getNombreGenero())).thenReturn(Optional.empty());
        when(generoMapper.generoRequestDTOToGenero(generoRequestDTO)).thenReturn(genero);
        when(generoRepository.save(any(Genero.class))).thenReturn(genero);
        when(generoMapper.generoToGeneroResponseDTO(genero)).thenReturn(generoResponseDTO);

        // Act
        GeneroResponseDTO resultado = generoService.crearGenero(generoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(generoResponseDTO.getNombreGenero(), resultado.getNombreGenero());
        verify(generoRepository).save(any(Genero.class));
    }

    @Test
    @DisplayName("Eliminar Genero Exitosamente si no está en uso")
    void eliminarGenero_cuandoNoEstaEnUso_deberiaCompletar() {
        // Arrange
        Long idExistente = 1L;
        // Asegurar que el mock de genero no tenga libros asociados para este test
        Genero generoSinLibros = new Genero();
        generoSinLibros.setIdGenero(idExistente);
        generoSinLibros.setNombreGenero("Test");
        generoSinLibros.setLibros(Collections.emptySet());

        when(generoRepository.findById(idExistente)).thenReturn(Optional.of(generoSinLibros));
        when(generoRepository.isGeneroEnUso(idExistente)).thenReturn(false);
        doNothing().when(generoRepository).delete(generoSinLibros);


        // Act & Assert
        assertDoesNotThrow(() -> generoService.eliminarGenero(idExistente));
        verify(generoRepository).delete(generoSinLibros);
    }

    @Test
    @DisplayName("Eliminar Genero Falla si está en uso")
    void eliminarGenero_cuandoEstaEnUso_deberiaLanzarBusinessRuleException() {
        // Arrange
        Long idExistente = 1L;
        when(generoRepository.findById(idExistente)).thenReturn(Optional.of(genero));
        when(generoRepository.isGeneroEnUso(idExistente)).thenReturn(true); // Mockear que está en uso

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            generoService.eliminarGenero(idExistente);
        });
        assertEquals("No se puede eliminar el género porque está asignado a uno o más libros.", exception.getMessage());
        verify(generoRepository, never()).delete(any(Genero.class));
    }
}


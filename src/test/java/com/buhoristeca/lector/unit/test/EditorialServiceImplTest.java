package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.dtos.EditorialRequestDTO;
import com.buhoristeca.lector.dtos.EditorialResponseDTO;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.mappers.EditorialMapper;
import com.buhoristeca.lector.model.Editorial;
import com.buhoristeca.lector.repositories.EditorialRepository;
import com.buhoristeca.lector.service.impl.EditorialServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EditorialServiceImplTest {

    @Mock
    private EditorialRepository editorialRepository;

    @Mock
    private EditorialMapper editorialMapper;

    @InjectMocks
    private EditorialServiceImpl editorialService;

    private Editorial editorial;
    private EditorialRequestDTO editorialRequestDTO;
    private EditorialResponseDTO editorialResponseDTO;

    @BeforeEach
    void setUp() {
        editorialRequestDTO = new EditorialRequestDTO("Nueva Editorial");

        editorial = new Editorial();
        editorial.setIdEditorial(1L);
        editorial.setNombreEditorial("Nueva Editorial");

        editorialResponseDTO = new EditorialResponseDTO();
        editorialResponseDTO.setIdEditorial(1L);
        editorialResponseDTO.setNombreEditorial("Nueva Editorial");
    }

    @Test
    @DisplayName("Crear Editorial Exitosamente")
    void crearEditorial_cuandoNoExiste_deberiaRetornarEditorialDTO() {
        // Arrange
        when(editorialRepository.findByNombreEditorial(editorialRequestDTO.getNombreEditorial())).thenReturn(Optional.empty());
        when(editorialMapper.editorialRequestDTOToEditorial(editorialRequestDTO)).thenReturn(editorial); // Asumimos que el ID no se setea aquí
        when(editorialRepository.save(any(Editorial.class))).thenReturn(editorial); // save devuelve la entidad con ID
        when(editorialMapper.editorialToEditorialResponseDTO(editorial)).thenReturn(editorialResponseDTO);

        // Act
        EditorialResponseDTO resultado = editorialService.crearEditorial(editorialRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(editorialResponseDTO.getIdEditorial(), resultado.getIdEditorial());
        assertEquals(editorialResponseDTO.getNombreEditorial(), resultado.getNombreEditorial());
        verify(editorialRepository).findByNombreEditorial(editorialRequestDTO.getNombreEditorial());
        verify(editorialRepository).save(any(Editorial.class));
        verify(editorialMapper).editorialRequestDTOToEditorial(editorialRequestDTO);
        verify(editorialMapper).editorialToEditorialResponseDTO(editorial);
    }

    @Test
    @DisplayName("Crear Editorial Falla si ya Existe")
    void crearEditorial_cuandoYaExiste_deberiaLanzarBusinessRuleException() {
        // Arrange
        when(editorialRepository.findByNombreEditorial(editorialRequestDTO.getNombreEditorial())).thenReturn(Optional.of(editorial));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            editorialService.crearEditorial(editorialRequestDTO);
        });
        assertEquals("Ya existe una editorial con el nombre: " + editorialRequestDTO.getNombreEditorial(), exception.getMessage());
        verify(editorialRepository).findByNombreEditorial(editorialRequestDTO.getNombreEditorial());
        verify(editorialRepository, never()).save(any(Editorial.class));
    }

    @Test
    @DisplayName("Listar Editoriales Retorna Lista Vacía si No Hay")
    void listarEditoriales_cuandoNoHay_deberiaRetornarListaVacia() {
        // Arrange
        when(editorialRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<EditorialResponseDTO> resultado = editorialService.listarEditoriales();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(editorialRepository).findAll();
    }

    @Test
    @DisplayName("Listar Editoriales Retorna Lista de DTOs")
    void listarEditoriales_cuandoExisten_deberiaRetornarListaDTOs() {
        // Arrange
        when(editorialRepository.findAll()).thenReturn(List.of(editorial));
        when(editorialMapper.editorialToEditorialResponseDTO(editorial)).thenReturn(editorialResponseDTO);

        // Act
        List<EditorialResponseDTO> resultado = editorialService.listarEditoriales();

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(editorialResponseDTO.getNombreEditorial(), resultado.get(0).getNombreEditorial());
        verify(editorialRepository).findAll();
    }

    @Test
    @DisplayName("Obtener Editorial Por ID Exitosamente")
    void obtenerEditorialPorId_cuandoExiste_deberiaRetornarDTO() {
        // Arrange
        Long idExistente = 1L;
        when(editorialRepository.findById(idExistente)).thenReturn(Optional.of(editorial));
        when(editorialMapper.editorialToEditorialResponseDTO(editorial)).thenReturn(editorialResponseDTO);

        // Act
        EditorialResponseDTO resultado = editorialService.obtenerEditorialPorId(idExistente);

        // Assert
        assertNotNull(resultado);
        assertEquals(editorialResponseDTO.getIdEditorial(), resultado.getIdEditorial());
        verify(editorialRepository).findById(idExistente);
    }

    @Test
    @DisplayName("Obtener Editorial Por ID Falla si No Existe")
    void obtenerEditorialPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        // Arrange
        Long idNoExistente = 99L;
        when(editorialRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            editorialService.obtenerEditorialPorId(idNoExistente);
        });
        assertEquals("Editorial no encontrada con ID: " + idNoExistente, exception.getMessage());
        verify(editorialRepository).findById(idNoExistente);
    }

    @Test
    @DisplayName("Actualizar Editorial Exitosamente")
    void actualizarEditorial_cuandoExisteYNombreNoDuplicado_deberiaRetornarDTOActualizado() {
        // Arrange
        Long idExistente = 1L;
        EditorialRequestDTO dtoActualizar = new EditorialRequestDTO("Editorial Actualizada");
        Editorial editorialActualizada = new Editorial(idExistente, "Editorial Actualizada");
        EditorialResponseDTO responseDTOActualizado = new EditorialResponseDTO();
        responseDTOActualizado.setIdEditorial(idExistente);
        responseDTOActualizado.setNombreEditorial("Editorial Actualizada");

        when(editorialRepository.findById(idExistente)).thenReturn(Optional.of(editorial)); // editorial original
        when(editorialRepository.findByNombreEditorial(dtoActualizar.getNombreEditorial())).thenReturn(Optional.empty()); // No hay otra con ese nombre
        when(editorialRepository.save(any(Editorial.class))).thenReturn(editorialActualizada);
        when(editorialMapper.editorialToEditorialResponseDTO(editorialActualizada)).thenReturn(responseDTOActualizado);

        // Act
        EditorialResponseDTO resultado = editorialService.actualizarEditorial(idExistente, dtoActualizar);

        // Assert
        assertNotNull(resultado);
        assertEquals("Editorial Actualizada", resultado.getNombreEditorial());
        verify(editorialRepository).findById(idExistente);
        verify(editorialRepository).findByNombreEditorial(dtoActualizar.getNombreEditorial());
        verify(editorialRepository).save(argThat(savedEditorial ->
                savedEditorial.getIdEditorial().equals(idExistente) &&
                        savedEditorial.getNombreEditorial().equals("Editorial Actualizada")
        ));
    }

    @Test
    @DisplayName("Actualizar Editorial Falla si ID No Existe")
    void actualizarEditorial_cuandoIdNoExiste_deberiaLanzarResourceNotFoundException() {
        // Arrange
        Long idNoExistente = 99L;
        when(editorialRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            editorialService.actualizarEditorial(idNoExistente, editorialRequestDTO);
        });
        verify(editorialRepository).findById(idNoExistente);
        verify(editorialRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar Editorial Falla si Nuevo Nombre ya Existe en Otra Editorial")
    void actualizarEditorial_cuandoNuevoNombreYaExisteEnOtra_deberiaLanzarBusinessRuleException() {
        // Arrange
        Long idExistente = 1L;
        EditorialRequestDTO dtoActualizar = new EditorialRequestDTO("Nombre Existente");
        Editorial otraEditorialConMismoNombre = new Editorial(2L, "Nombre Existente");

        when(editorialRepository.findById(idExistente)).thenReturn(Optional.of(editorial));
        when(editorialRepository.findByNombreEditorial(dtoActualizar.getNombreEditorial())).thenReturn(Optional.of(otraEditorialConMismoNombre));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> {
            editorialService.actualizarEditorial(idExistente, dtoActualizar);
        });
        verify(editorialRepository).findById(idExistente);
        verify(editorialRepository).findByNombreEditorial(dtoActualizar.getNombreEditorial());
        verify(editorialRepository, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar Editorial Exitosamente")
    void eliminarEditorial_cuandoExisteYNoEstaEnUso_deberiaCompletarSinErrores() {
        // Arrange
        Long idExistente = 1L;
        when(editorialRepository.findById(idExistente)).thenReturn(Optional.of(editorial)); // Devuelve la entidad para que deleteById no falle internamente

        // Act
        assertDoesNotThrow(() -> editorialService.eliminarEditorial(idExistente));

        // Assert
        verify(editorialRepository).findById(idExistente);
        verify(editorialRepository).delete(editorial); // Verifica que se llama delete con la entidad
    }

    @Test
    @DisplayName("Eliminar Editorial Falla si No Existe")
    void eliminarEditorial_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        // Arrange
        Long idNoExistente = 99L;
        when(editorialRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            editorialService.eliminarEditorial(idNoExistente);
        });
        verify(editorialRepository).findById(idNoExistente);
        verify(editorialRepository, never()).deleteById(anyLong());
        verify(editorialRepository, never()).delete(any(Editorial.class));
    }

    @Test
    @DisplayName("Eliminar Editorial Falla si Está en Uso (DataIntegrityViolation)")
    void eliminarEditorial_cuandoEstaEnUso_deberiaLanzarBusinessRuleException() {
        // Arrange
        Long idExistente = 1L;
        when(editorialRepository.findById(idExistente)).thenReturn(Optional.of(editorial));
        doThrow(DataIntegrityViolationException.class).when(editorialRepository).delete(editorial);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            editorialService.eliminarEditorial(idExistente);
        });
        assertTrue(exception.getMessage().contains("No se puede eliminar la editorial. Puede estar asignada a libros."));
        verify(editorialRepository).findById(idExistente);
        verify(editorialRepository).delete(editorial);
    }
}
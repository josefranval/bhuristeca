package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.dtos.IdiomaRequestDTO;
import com.buhoristeca.lector.dtos.IdiomaResponseDTO;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.mappers.IdiomaMapper;
import com.buhoristeca.lector.model.Idioma;
import com.buhoristeca.lector.repositories.IdiomaRepository;
import com.buhoristeca.lector.service.impl.IdiomaServiceImpl;
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
class IdiomaServiceImplTest {

    @Mock
    private IdiomaRepository idiomaRepository;

    @Mock
    private IdiomaMapper idiomaMapper;

    @InjectMocks
    private IdiomaServiceImpl idiomaService;

    private Idioma idioma;
    private IdiomaRequestDTO idiomaRequestDTO;
    private IdiomaResponseDTO idiomaResponseDTO;

    @BeforeEach
    void setUp() {
        idiomaRequestDTO = new IdiomaRequestDTO("Español Test");

        idioma = new Idioma();
        idioma.setIdIdioma(1L);
        idioma.setNombreIdioma("Español Test");

        idiomaResponseDTO = new IdiomaResponseDTO();
        idiomaResponseDTO.setIdIdioma(1L);
        idiomaResponseDTO.setNombreIdioma("Español Test");
    }

    @Test
    @DisplayName("Crear Idioma Exitosamente")
    void crearIdioma_cuandoNoExiste_deberiaRetornarIdiomaDTO() {
        // Arrange
        when(idiomaRepository.findByNombreIdioma(idiomaRequestDTO.getNombreIdioma())).thenReturn(Optional.empty());
        when(idiomaMapper.idiomaRequestDTOToIdioma(idiomaRequestDTO)).thenReturn(idioma);
        when(idiomaRepository.save(any(Idioma.class))).thenReturn(idioma);
        when(idiomaMapper.idiomaToIdiomaResponseDTO(idioma)).thenReturn(idiomaResponseDTO);

        // Act
        IdiomaResponseDTO resultado = idiomaService.crearIdioma(idiomaRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(idiomaResponseDTO.getIdIdioma(), resultado.getIdIdioma());
        assertEquals(idiomaResponseDTO.getNombreIdioma(), resultado.getNombreIdioma());
        verify(idiomaRepository).save(any(Idioma.class));
    }

    @Test
    @DisplayName("Crear Idioma Falla si ya Existe")
    void crearIdioma_cuandoYaExiste_deberiaLanzarBusinessRuleException() {
        // Arrange
        when(idiomaRepository.findByNombreIdioma(idiomaRequestDTO.getNombreIdioma())).thenReturn(Optional.of(idioma));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> {
            idiomaService.crearIdioma(idiomaRequestDTO);
        });
        verify(idiomaRepository, never()).save(any(Idioma.class));
    }

    @Test
    @DisplayName("Listar Idiomas Retorna Lista de DTOs")
    void listarIdiomas_cuandoExisten_deberiaRetornarListaDTOs() {
        // Arrange
        when(idiomaRepository.findAll()).thenReturn(List.of(idioma));
        when(idiomaMapper.idiomaToIdiomaResponseDTO(idioma)).thenReturn(idiomaResponseDTO);

        // Act
        List<IdiomaResponseDTO> resultado = idiomaService.listarIdiomas();

        // Assert
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(idiomaResponseDTO.getNombreIdioma(), resultado.get(0).getNombreIdioma());
    }


    @Test
    @DisplayName("Obtener Idioma Por ID Exitosamente")
    void obtenerIdiomaPorId_cuandoExiste_deberiaRetornarDTO() {
        // Arrange
        Long idExistente = 1L;
        when(idiomaRepository.findById(idExistente)).thenReturn(Optional.of(idioma));
        when(idiomaMapper.idiomaToIdiomaResponseDTO(idioma)).thenReturn(idiomaResponseDTO);

        // Act
        IdiomaResponseDTO resultado = idiomaService.obtenerIdiomaPorId(idExistente);

        // Assert
        assertNotNull(resultado);
        assertEquals(idiomaResponseDTO.getIdIdioma(), resultado.getIdIdioma());
    }

    @Test
    @DisplayName("Obtener Idioma Por ID Falla si No Existe")
    void obtenerIdiomaPorId_cuandoNoExiste_deberiaLanzarResourceNotFoundException() {
        // Arrange
        Long idNoExistente = 99L;
        when(idiomaRepository.findById(idNoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            idiomaService.obtenerIdiomaPorId(idNoExistente);
        });
    }

    @Test
    @DisplayName("Actualizar Idioma Exitosamente")
    void actualizarIdioma_cuandoExiste_deberiaRetornarDTOActualizado() {
        // Arrange
        Long idExistente = 1L;
        IdiomaRequestDTO dtoActualizar = new IdiomaRequestDTO("Inglés Test");
        Idioma idiomaActualizadoEntidad = new Idioma(idExistente, "Inglés Test");
        IdiomaResponseDTO responseDTOActualizado = new IdiomaResponseDTO();
        responseDTOActualizado.setIdIdioma(idExistente);
        responseDTOActualizado.setNombreIdioma("Inglés Test");


        when(idiomaRepository.findById(idExistente)).thenReturn(Optional.of(idioma));
        when(idiomaRepository.findByNombreIdioma(dtoActualizar.getNombreIdioma())).thenReturn(Optional.empty());
        when(idiomaRepository.save(any(Idioma.class))).thenReturn(idiomaActualizadoEntidad);
        when(idiomaMapper.idiomaToIdiomaResponseDTO(idiomaActualizadoEntidad)).thenReturn(responseDTOActualizado);

        // Act
        IdiomaResponseDTO resultado = idiomaService.actualizarIdioma(idExistente, dtoActualizar);

        // Assert
        assertNotNull(resultado);
        assertEquals("Inglés Test", resultado.getNombreIdioma());
        verify(idiomaRepository).save(argThat(i -> i.getNombreIdioma().equals("Inglés Test")));
    }

    @Test
    @DisplayName("Eliminar Idioma Exitosamente")
    void eliminarIdioma_cuandoExisteYNoEstaEnUso_deberiaCompletar() {
        // Arrange
        Long idExistente = 1L;
        when(idiomaRepository.findById(idExistente)).thenReturn(Optional.of(idioma));
        doNothing().when(idiomaRepository).delete(idioma);

        // Act & Assert
        assertDoesNotThrow(() -> idiomaService.eliminarIdioma(idExistente));
        verify(idiomaRepository).delete(idioma);
    }

    @Test
    @DisplayName("Eliminar Idioma Falla si Está en Uso")
    void eliminarIdioma_cuandoEstaEnUso_deberiaLanzarBusinessRuleException() {
        // Arrange
        Long idExistente = 1L;
        when(idiomaRepository.findById(idExistente)).thenReturn(Optional.of(idioma));
        doThrow(DataIntegrityViolationException.class).when(idiomaRepository).delete(idioma);

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> {
            idiomaService.eliminarIdioma(idExistente);
        });
    }
}

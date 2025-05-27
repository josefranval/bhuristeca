package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.dtos.RolUsuarioRequestDTO;
import com.buhoristeca.lector.dtos.RolUsuarioResponseDTO;
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.mappers.RolUsuarioMapper;
import com.buhoristeca.lector.model.RolUsuario;
import com.buhoristeca.lector.repositories.RolUsuarioRepository;
import com.buhoristeca.lector.repositories.UsuarioRepository;
import com.buhoristeca.lector.service.impl.RolUsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolUsuarioServiceImplTest {

    @Mock
    private RolUsuarioRepository rolUsuarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolUsuarioMapper rolUsuarioMapper;

    @InjectMocks
    private RolUsuarioServiceImpl rolUsuarioService;

    private RolUsuario rolUsuario;
    private RolUsuarioRequestDTO rolUsuarioRequestDTO;
    private RolUsuarioResponseDTO rolUsuarioResponseDTO;

    @BeforeEach
    void setUp() {
        rolUsuarioRequestDTO = new RolUsuarioRequestDTO("ROL_TEST");

        rolUsuario = new RolUsuario();
        rolUsuario.setIdRolUsuario(1L);
        rolUsuario.setNombreRol("ROL_TEST");

        rolUsuarioResponseDTO = new RolUsuarioResponseDTO();
        rolUsuarioResponseDTO.setIdRolUsuario(1L);
        rolUsuarioResponseDTO.setNombreRol("ROL_TEST");
    }

    @Test
    @DisplayName("Crear RolUsuario Exitosamente")
    void crearRolUsuario_cuandoNoExiste_deberiaRetornarDTO() {
        // Arrange
        when(rolUsuarioRepository.findByNombreRol(anyString())).thenReturn(Optional.empty());
        when(rolUsuarioMapper.rolUsuarioRequestDTOToRolUsuario(any(RolUsuarioRequestDTO.class))).thenReturn(rolUsuario);
        when(rolUsuarioRepository.save(any(RolUsuario.class))).thenReturn(rolUsuario);
        when(rolUsuarioMapper.rolUsuarioToRolUsuarioResponseDTO(any(RolUsuario.class))).thenReturn(rolUsuarioResponseDTO);

        // Act
        RolUsuarioResponseDTO result = rolUsuarioService.crearRolUsuario(rolUsuarioRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ROL_TEST", result.getNombreRol());
        verify(rolUsuarioRepository).save(any(RolUsuario.class));
    }

    @Test
    @DisplayName("Eliminar RolUsuario Falla si está en uso")
    void eliminarRolUsuario_cuandoEstaEnUso_deberiaLanzarBusinessRuleException() {
        // Arrange
        Long idExistente = 1L;
        when(rolUsuarioRepository.findById(idExistente)).thenReturn(Optional.of(rolUsuario));
        when(usuarioRepository.existsByRolUsuario(rolUsuario)).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> {
            rolUsuarioService.eliminarRolUsuario(idExistente);
        });
        verify(rolUsuarioRepository, never()).delete(any(RolUsuario.class));
    }
}

package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.dtos.UsuarioRequestDTO;
import com.buhoristeca.lector.dtos.UsuarioResponseDTO;
import com.buhoristeca.lector.dtos.RolUsuarioResponseDTO; // Para el DTO anidado
import com.buhoristeca.lector.exceptions.BusinessRuleException;
import com.buhoristeca.lector.exceptions.ResourceNotFoundException;
import com.buhoristeca.lector.mappers.UsuarioMapper;
import com.buhoristeca.lector.model.RolUsuario;
import com.buhoristeca.lector.model.Usuario;
import com.buhoristeca.lector.repositories.RolUsuarioRepository;
import com.buhoristeca.lector.repositories.UsuarioRepository;
import com.buhoristeca.lector.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolUsuarioRepository rolUsuarioRepository;
    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioRequestDTO usuarioRequestDTO;
    private Usuario usuario;
    private UsuarioResponseDTO usuarioResponseDTO;
    private RolUsuario rolUsuario;
    private RolUsuarioResponseDTO rolUsuarioResponseDTO;

    @BeforeEach
    void setUp() {
        rolUsuario = new RolUsuario(1L, "ESTUDIANTE");

        rolUsuarioResponseDTO = new RolUsuarioResponseDTO();
        rolUsuarioResponseDTO.setIdRolUsuario(1L);
        rolUsuarioResponseDTO.setNombreRol("ESTUDIANTE");

        usuarioRequestDTO = new UsuarioRequestDTO("Juan", "Perez", "12345678X", "juan.perez@example.com", 1L);

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        usuario.setDni("12345678X");
        usuario.setCorreoElectronico("juan.perez@example.com");
        usuario.setRolUsuario(rolUsuario);
        usuario.setActivo(true);

        usuarioResponseDTO = new UsuarioResponseDTO();
        usuarioResponseDTO.setIdUsuario(1L);
        usuarioResponseDTO.setNombre("Juan");
        usuarioResponseDTO.setApellido("Perez");
        usuarioResponseDTO.setDni("12345678X");
        usuarioResponseDTO.setCorreoElectronico("juan.perez@example.com");
        usuarioResponseDTO.setRolUsuario(rolUsuarioResponseDTO);
        usuarioResponseDTO.setActivo(true);
    }

    @Test
    @DisplayName("Registrar Usuario Exitosamente")
    void registrarUsuario_cuandoDatosValidos_deberiaRetornarUsuarioResponseDTO() {
        // Arrange
        when(usuarioRepository.existsByDni(anyString())).thenReturn(false);
        when(usuarioRepository.existsByCorreoElectronico(anyString())).thenReturn(false);
        when(rolUsuarioRepository.findById(anyLong())).thenReturn(Optional.of(rolUsuario));
        when(usuarioMapper.usuarioRequestDTOToUsuario(any(UsuarioRequestDTO.class), any(RolUsuario.class))).thenReturn(usuario);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.usuarioToUsuarioResponseDTO(any(Usuario.class))).thenReturn(usuarioResponseDTO);

        // Act
        UsuarioResponseDTO resultado = usuarioService.registrarUsuario(usuarioRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(usuarioRequestDTO.getNombre(), resultado.getNombre());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar Usuario Falla si DNI ya existe")
    void registrarUsuario_cuandoDniExiste_deberiaLanzarBusinessRuleException() {
        // Arrange
        when(usuarioRepository.existsByDni(usuarioRequestDTO.getDni())).thenReturn(true);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            usuarioService.registrarUsuario(usuarioRequestDTO);
        });
        assertEquals("Ya existe un usuario con el DNI: " + usuarioRequestDTO.getDni(), exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Consultar Usuario por ID Exitosamente")
    void consultarUsuarioPorId_cuandoExisteYActivo_deberiaRetornarDTO() {
        // Arrange
        Long idExistente = 1L;
        when(usuarioRepository.findByIdUsuarioAndActivoTrue(idExistente)).thenReturn(Optional.of(usuario));
        when(usuarioMapper.usuarioToUsuarioResponseDTO(usuario)).thenReturn(usuarioResponseDTO);

        // Act
        UsuarioResponseDTO resultado = usuarioService.consultarUsuarioPorId(idExistente);

        // Assert
        assertNotNull(resultado);
        assertEquals(idExistente, resultado.getIdUsuario());
        verify(usuarioRepository).findByIdUsuarioAndActivoTrue(idExistente);
    }

    @Test
    @DisplayName("Consultar Usuario por ID Falla si No Existe o No Activo")
    void consultarUsuarioPorId_cuandoNoExisteONoActivo_deberiaLanzarResourceNotFoundException() {
        // Arrange
        Long idNoExistente = 99L;
        when(usuarioRepository.findByIdUsuarioAndActivoTrue(idNoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.consultarUsuarioPorId(idNoExistente);
        });
    }

    @Test
    @DisplayName("Consultar Usuarios Devuelve Página de DTOs")
    void consultarUsuarios_deberiaRetornarPaginaDeDTOs() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Usuario> listaUsuarios = List.of(usuario);
        Page<Usuario> paginaUsuarios = new PageImpl<>(listaUsuarios, pageable, listaUsuarios.size());

        when(usuarioRepository.findAllByActivoTrue(pageable)).thenReturn(paginaUsuarios);
        when(usuarioMapper.usuarioToUsuarioResponseDTO(usuario)).thenReturn(usuarioResponseDTO); // Para el mapeo dentro del .map()

        // Act
        Page<UsuarioResponseDTO> resultado = usuarioService.consultarUsuarios(null, null, pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(usuarioResponseDTO.getNombre(), resultado.getContent().get(0).getNombre());
        verify(usuarioRepository).findAllByActivoTrue(pageable);
    }

    @Test
    @DisplayName("Baja Lógica Usuario Exitosamente")
    void bajaLogicaUsuario_cuandoUsuarioExiste_deberiaMarcarComoInactivo() {
        // Arrange
        Long idExistente = 1L;
        Usuario usuarioParaBaja = new Usuario();
        usuarioParaBaja.setIdUsuario(idExistente);
        usuarioParaBaja.setActivo(true);

        when(usuarioRepository.findById(idExistente)).thenReturn(Optional.of(usuarioParaBaja));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // Act
        usuarioService.bajaLogicaUsuario(idExistente);

        // Assert
        verify(usuarioRepository).findById(idExistente);
        verify(usuarioRepository).save(argThat(savedUsuario ->
                !savedUsuario.getActivo() && savedUsuario.getIdUsuario().equals(idExistente)
        ));
        assertFalse(usuarioParaBaja.getActivo());
    }

}
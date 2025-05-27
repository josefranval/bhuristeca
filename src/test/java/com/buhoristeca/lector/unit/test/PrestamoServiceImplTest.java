package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.dtos.*;
import com.buhoristeca.lector.exceptions.*;
import com.buhoristeca.lector.mappers.PrestamoMapper;
import com.buhoristeca.lector.model.*;
import com.buhoristeca.lector.model.enums.EstadoPrestamo;
import com.buhoristeca.lector.repositories.*;
import com.buhoristeca.lector.service.impl.PrestamoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.openapitools.jackson.nullable.JsonNullable;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrestamoServiceImplTest {
    @Mock private PrestamoRepository prestamoRepository;
    @Mock private LibroRepository libroRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PrestamoMapper prestamoMapper;

    @InjectMocks private PrestamoServiceImpl prestamoService;

    private Usuario usuario;
    private Libro libro;
    private PrestamoRequestDTO prestamoRequestDTO;
    private Prestamo prestamo;
    private PrestamoResponseDTO prestamoResponseDTO;
    private Long usuarioId = 1L;
    private Long libroId = 1L;
    private Long prestamoId = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(prestamoService, "diasPrestamoPorDefecto", 14);

        usuario = new Usuario();
        usuario.setIdUsuario(usuarioId);
        usuario.setActivo(true);

        libro = new Libro();
        libro.setIdLibro(libroId);
        libro.setActivo(true);
        libro.setCantidadEjemplares(5);

        prestamoRequestDTO = new PrestamoRequestDTO();
        prestamoRequestDTO.setIdUsuario(usuarioId);
        prestamoRequestDTO.setIdLibro(libroId);
        prestamoRequestDTO.setPlazoPrestamoDias(JsonNullable.of(7));

        prestamo = new Prestamo();
        prestamo.setIdPrestamo(prestamoId);
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);
        prestamo.setFechaPrestamo(LocalDateTime.now());
        prestamo.setFechaDevolucionEsperada(LocalDate.now().plusDays(7));
        prestamo.setEstadoPrestamo(EstadoPrestamo.PRESTADO);

        LibroSummaryResponseDTO libroSummary = new LibroSummaryResponseDTO();
        libroSummary.setIdLibro(libroId);
        UsuarioSummaryResponseDTO usuarioSummary = new UsuarioSummaryResponseDTO();
        usuarioSummary.setIdUsuario(usuarioId);

        prestamoResponseDTO = new PrestamoResponseDTO();
        prestamoResponseDTO.setIdPrestamo(prestamoId);
        prestamoResponseDTO.setLibro(libroSummary);
        prestamoResponseDTO.setUsuario(usuarioSummary);
        prestamoResponseDTO.setEstadoPrestamo(EstadoPrestamoDTO.PRESTADO); // DTO Enum
    }

    @Test
    @DisplayName("Registrar Préstamo Exitosamente")
    void registrarPrestamo_cuandoValido_deberiaRetornarPrestamoResponseDTO() {
        // Arrange
        when(usuarioRepository.findByIdUsuarioAndActivoTrue(usuarioId)).thenReturn(Optional.of(usuario));
        when(libroRepository.findByIdLibroAndActivoTrue(libroId)).thenReturn(Optional.of(libro));
        when(prestamoRepository.existsByUsuarioAndEstadoPrestamo(usuario, EstadoPrestamo.VENCIDO)).thenReturn(false);
        when(prestamoRepository.countByLibroAndEstadoPrestamoIn(eq(libro), anyList())).thenReturn(0L); // No hay otros préstamos activos para este libro
        when(prestamoRepository.existsByUsuarioAndLibroAndEstadoPrestamoIn(eq(usuario), eq(libro), anyList())).thenReturn(false);

        Prestamo prestamoSinId = new Prestamo(); // Entidad antes de guardar
        prestamoSinId.setUsuario(usuario);
        prestamoSinId.setLibro(libro);

        when(prestamoMapper.prestamoRequestDTOToPrestamo(prestamoRequestDTO, libro, usuario)).thenReturn(prestamoSinId);
        when(prestamoRepository.save(any(Prestamo.class))).thenAnswer(invocation -> {
            Prestamo p = invocation.getArgument(0);
            p.setIdPrestamo(prestamoId);
            p.setFechaPrestamo(LocalDateTime.now().minusSeconds(1));
            p.setFechaDevolucionEsperada(LocalDate.now().plusDays(prestamoRequestDTO.getPlazoPrestamoDias().orElse(14)));
            p.setEstadoPrestamo(EstadoPrestamo.PRESTADO);
            return p;
        });
        when(prestamoMapper.prestamoToPrestamoResponseDTO(any(Prestamo.class))).thenReturn(prestamoResponseDTO);

        // Act
        PrestamoResponseDTO resultado = prestamoService.registrarPrestamo(prestamoRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoPrestamoDTO.PRESTADO, resultado.getEstadoPrestamo());
        verify(prestamoRepository).save(any(Prestamo.class));
    }
}
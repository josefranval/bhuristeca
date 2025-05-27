package com.buhoristeca.lector.unit.test;

import com.buhoristeca.lector.dtos.*;
import com.buhoristeca.lector.mappers.InformeMapper;
import com.buhoristeca.lector.mappers.LibroMapper;
import com.buhoristeca.lector.mappers.PrestamoMapper;
import com.buhoristeca.lector.model.Libro;
import com.buhoristeca.lector.model.enums.EstadoPrestamo;
import com.buhoristeca.lector.repositories.LibroRepository;
import com.buhoristeca.lector.repositories.PrestamoRepository;
import com.buhoristeca.lector.service.LibroService;
import com.buhoristeca.lector.service.impl.InformeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InformeServiceImplTest {

    @Mock private PrestamoRepository prestamoRepository;
    @Mock private LibroRepository libroRepository;
    @Mock private LibroService libroService; // Se mockea la interfaz del servicio
    @Mock private PrestamoMapper prestamoMapper;
    @Mock private LibroMapper libroMapper;
    @Mock private InformeMapper informeMapper;

    @InjectMocks private InformeServiceImpl informeService;

    private Libro libro1;
    private LibroResponseDTO libroResponseDTO1;
    private InformeDisponibilidadLibroDTO informeDispLibro1;

    @BeforeEach
    void setUp() {
        libro1 = new Libro();
        libro1.setIdLibro(1L);
        libro1.setTitulo("Libro Test Informe");
        libro1.setCantidadEjemplares(5);
        libro1.setActivo(true);

        LibroSummaryResponseDTO libroSummary = new LibroSummaryResponseDTO();
        libroSummary.setIdLibro(1L);
        libroSummary.setTitulo("Libro Test Informe");

        informeDispLibro1 = new InformeDisponibilidadLibroDTO();
        informeDispLibro1.setLibro(libroSummary);
        informeDispLibro1.setCantidadTotal(5);
        informeDispLibro1.setCantidadDisponible(3);
        informeDispLibro1.setCantidadPrestados(2);

        libroResponseDTO1 = new LibroResponseDTO();
    }

    @Test
    @DisplayName("Generar Reporte Disponibilidad Catálogo para un Libro Específico")
    void generarReporteDisponibilidadCatalogo_conIdLibro_deberiaLlamarAServicioLibro() {
        // Arrange
        when(libroService.obtenerDisponibilidadLibro(1L)).thenReturn(informeDispLibro1);

        // Act
        List<InformeDisponibilidadLibroDTO> resultado = informeService.generarReporteDisponibilidadCatalogo(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(informeDispLibro1, resultado.get(0));
        verify(libroService).obtenerDisponibilidadLibro(1L);
    }

    @Test
    @DisplayName("Generar Reporte Disponibilidad Catálogo Completo")
    void generarReporteDisponibilidadCatalogo_sinIdLibro_deberiaLlamarAServicioLibro() {
        // Arrange
        when(libroService.obtenerDisponibilidadCatalogoCompleto()).thenReturn(List.of(informeDispLibro1));

        // Act
        List<InformeDisponibilidadLibroDTO> resultado = informeService.generarReporteDisponibilidadCatalogo(null);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(informeDispLibro1, resultado.get(0));
        verify(libroService).obtenerDisponibilidadCatalogoCompleto();
    }

    @Test
    @DisplayName("Generar Reporte Libros Menos Prestados")
    void generarReporteLibrosMenosPrestados_deberiaRetornarListaLibros() {
        // Arrange
        LocalDate fechaDesde = LocalDate.of(2023, 1, 1);
        LocalDate fechaHasta = LocalDate.of(2023, 12, 31);
        List<Long> idsPrestados = List.of(10L, 11L); // IDs de libros que sí fueron prestados
        List<Libro> librosNoPrestados = List.of(libro1); // libro1 no está en idsPrestados

        when(prestamoRepository.findLibrosPrestadosEntreFechas(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(idsPrestados);
        when(libroRepository.findByIdLibroNotInAndActivoTrue(idsPrestados)).thenReturn(librosNoPrestados);
        when(libroMapper.libroToLibroResponseDTO(libro1)).thenReturn(libroResponseDTO1);

        // Act
        List<LibroResponseDTO> resultado = informeService.generarReporteLibrosMenosPrestados(fechaDesde, fechaHasta);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(libroResponseDTO1, resultado.get(0));
    }
}
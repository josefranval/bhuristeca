package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.InformeDisponibilidadLibroDTO;
import com.buhoristeca.lector.dtos.LibroResponseDTO;
import com.buhoristeca.lector.dtos.PrestamoResponseDTO;
import com.buhoristeca.lector.dtos.ReportePopularidadGeneros200ResponseInnerDTO;

import java.time.LocalDate;
import java.util.List;

public interface InformeService {
    List<PrestamoResponseDTO> generarReportePrestamosPorTipoUsuario(Long idRolUsuario, LocalDate fechaDesde, LocalDate fechaHasta);
    List<InformeDisponibilidadLibroDTO> generarReporteDisponibilidadCatalogo(Long idLibro);
    List<LibroResponseDTO> generarReporteLibrosMenosPrestados(LocalDate fechaDesde, LocalDate fechaHasta);
    List<PrestamoResponseDTO> generarReportePrestamosVencidos();
    List<ReportePopularidadGeneros200ResponseInnerDTO> generarReportePopularidadGeneros(LocalDate fechaDesde, LocalDate fechaHasta);
}

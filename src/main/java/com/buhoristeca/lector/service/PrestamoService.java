package com.buhoristeca.lector.service;

import com.buhoristeca.lector.dtos.PrestamoRequestDTO;
import com.buhoristeca.lector.dtos.PrestamoResponseDTO;
import com.buhoristeca.lector.dtos.EstadoPrestamoDTO;
// import org.springframework.data.domain.Page; // No longer needed for listarTodosPrestamos
// import org.springframework.data.domain.Pageable; // No longer needed for listarTodosPrestamos

import java.time.LocalDate;
import java.util.List;

public interface PrestamoService {
    PrestamoResponseDTO registrarPrestamo(PrestamoRequestDTO prestamoRequestDTO);
    PrestamoResponseDTO registrarDevolucion(Long idPrestamo);
    // Changed to return List and no Pageable, to match PrestamosResource
    List<PrestamoResponseDTO> listarTodosPrestamos(Long idUsuario, Long idLibro, EstadoPrestamoDTO estadoDTO, LocalDate fechaDesde, LocalDate fechaHasta);
    List<PrestamoResponseDTO> consultarPrestamosPorUsuario(Long idUsuario, boolean soloActivos);
    void verificarYActualizarPrestamosVencidos();
    List<PrestamoResponseDTO> obtenerPrestamosVencidos();
}
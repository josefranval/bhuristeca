package com.buhoristeca.lector.repositories;

import com.buhoristeca.lector.model.Prestamo; // Paquete corregido
import com.buhoristeca.lector.model.Usuario; // Paquete corregido
import com.buhoristeca.lector.model.Libro;   // Paquete corregido
import com.buhoristeca.lector.model.enums.EstadoPrestamo; // Paquete corregido
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {
    boolean existsByUsuarioAndEstadoPrestamo(Usuario usuario, EstadoPrestamo estado);
    long countByLibroAndEstadoPrestamoIn(Libro libro, List<EstadoPrestamo> estados);
    boolean existsByUsuarioAndLibroAndEstadoPrestamoIn(Usuario usuario, Libro libro, List<EstadoPrestamo> estados);
    List<Prestamo> findByUsuarioAndEstadoPrestamoIn(Usuario usuario, List<EstadoPrestamo> estados);
    List<Prestamo> findByUsuario(Usuario usuario);
    List<Prestamo> findByEstadoPrestamoAndFechaDevolucionEsperadaBefore(EstadoPrestamo estado, LocalDate fecha);
    List<Prestamo> findByEstadoPrestamo(EstadoPrestamo estado);
    long countByLibroIdLibroAndEstadoPrestamoIn(Long idLibro, List<EstadoPrestamo> estados);

    // Para informe de libros menos prestados
    @Query("SELECT DISTINCT p.libro.idLibro FROM Prestamo p WHERE p.fechaPrestamo BETWEEN :fechaDesde AND :fechaHasta")
    List<Long> findLibrosPrestadosEntreFechas(@Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);

    // Para informe de popularidad de géneros
    @Query("SELECT g.idGenero, g.nombreGenero, COUNT(p.idPrestamo) " +
            "FROM Prestamo p JOIN p.libro l JOIN l.generos g " +
            "WHERE (:fechaDesde IS NULL OR p.fechaPrestamo >= :fechaDesde) " +
            "AND (:fechaHasta IS NULL OR p.fechaPrestamo < :fechaHasta) " +
            "GROUP BY g.idGenero, g.nombreGenero " +
            "ORDER BY COUNT(p.idPrestamo) DESC")
    List<Object[]> countPrestamosPorGeneroEntreFechas(
            @Param("fechaDesde") LocalDateTime fechaDesde,
            @Param("fechaHasta") LocalDateTime fechaHasta
    );
}

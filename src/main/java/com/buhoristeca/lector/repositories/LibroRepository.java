package com.buhoristeca.lector.repositories;

import com.buhoristeca.lector.model.Libro; // Paquete corregido
import com.buhoristeca.lector.model.Genero; // Paquete corregido
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    boolean existsByIsbnAndActivoTrue(String isbn);
    Page<Libro> findByIsbnAndActivoTrue(String isbn, Pageable pageable);
    Page<Libro> findByTituloContainingIgnoreCaseAndActivoTrue(String titulo, Pageable pageable);
    Page<Libro> findByAutorContainingIgnoreCaseAndActivoTrue(String autor, Pageable pageable);
    Page<Libro> findByGenerosContainsAndActivoTrue(Genero genero, Pageable pageable);
    Page<Libro> findAllByActivoTrue(Pageable pageable);
    List<Libro> findAllByActivoTrue(); // Para el informe de disponibilidad
    Optional<Libro> findByIdLibroAndActivoTrue(Long idLibro);
    @Query("SELECT l FROM Libro l WHERE l.activo = true AND l.idLibro NOT IN :idsExcluidos")
    List<Libro> findByIdLibroNotInAndActivoTrue(@Param("idsExcluidos") List<Long> idsExcluidos);
}
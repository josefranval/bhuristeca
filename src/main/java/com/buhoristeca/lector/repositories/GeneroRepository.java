package com.buhoristeca.lector.repositories;

import com.buhoristeca.lector.model.Genero; // Paquete corregido
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GeneroRepository extends JpaRepository<Genero, Long> {
    Optional<Genero> findByNombreGenero(String nombreGenero);

    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END FROM Libro l JOIN l.generos g WHERE g.idGenero = :idGenero")
    boolean isGeneroEnUso(@Param("idGenero") Long idGenero);
}
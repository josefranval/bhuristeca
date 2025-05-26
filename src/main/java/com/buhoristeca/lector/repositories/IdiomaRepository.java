package com.buhoristeca.lector.repositories;

import com.buhoristeca.lector.model.Idioma; // Paquete corregido
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface IdiomaRepository extends JpaRepository<Idioma, Long> {
    Optional<Idioma> findByNombreIdioma(String nombreIdioma);
}
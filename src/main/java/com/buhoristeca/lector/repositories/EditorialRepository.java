package com.buhoristeca.lector.repositories;

import com.buhoristeca.lector.model.Editorial; // Paquete corregido
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {
    Optional<Editorial> findByNombreEditorial(String nombreEditorial);
}
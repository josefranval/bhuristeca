package com.buhoristeca.lector.repositories;

import com.buhoristeca.lector.model.RolUsuario; // Paquete corregido
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {
    Optional<RolUsuario> findByNombreRol(String nombreRol);
}
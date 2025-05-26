package com.buhoristeca.lector.repositories;

import com.buhoristeca.lector.model.Usuario; // Paquete corregido
import com.buhoristeca.lector.model.RolUsuario; // Paquete corregido
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByDni(String dni);
    boolean existsByCorreoElectronico(String correoElectronico);
    Page<Usuario> findByDniContainingAndActivoTrue(String dni, Pageable pageable);
    Page<Usuario> findByCorreoElectronicoContainingAndActivoTrue(String email, Pageable pageable);
    Page<Usuario> findAllByActivoTrue(Pageable pageable);
    Optional<Usuario> findByIdUsuarioAndActivoTrue(Long idUsuario);
    boolean existsByRolUsuario(RolUsuario rolUsuario); // Para verificar si el rol está en uso
}

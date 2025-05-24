package com.buhoristeca.lector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "roles_usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_usuario")
    private Long idRolUsuario;

    @Column(name = "nombre_rol", nullable = false, length = 50, unique = true)
    private String nombreRol; // e.g., "ESTUDIANTE", "PROFESOR", "PUBLICO_GENERAL"

    // Optional: inverse side for Usuario's ManyToOne
    // @OneToMany(mappedBy = "rolUsuario")
    // private Set<Usuario> usuarios;
}
package com.buhoristeca.lector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 20, unique = true)
    private String dni;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 255)
    private String direccion;

    @Column(name = "correo_electronico", nullable = false, length = 150, unique = true)
    private String correoElectronico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol_usuario", referencedColumnName = "id_rol_usuario", nullable = false)
    private RolUsuario rolUsuario;

    @Column(nullable = false)
    private Boolean activo = true; // delete

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Prestamo> prestamos = new HashSet<>();
}
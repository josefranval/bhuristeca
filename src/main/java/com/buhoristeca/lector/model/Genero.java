package com.buhoristeca.lector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "generos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genero")
    private Long idGenero;

    @Column(name = "nombre_genero", nullable = false, length = 100, unique = true)
    private String nombreGenero;

    @ManyToMany(mappedBy = "generos")
    @EqualsAndHashCode.Exclude // To avoid issues in collections
    @ToString.Exclude // To avoid issues in logging/debugging
    private Set<Libro> libros = new HashSet<>();
}
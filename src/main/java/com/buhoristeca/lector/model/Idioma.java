package com.buhoristeca.lector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Entity
@Table(name = "idiomas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Idioma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_idioma")
    private Long idIdioma;

    @Column(name = "nombre_idioma", nullable = false, length = 50, unique = true)
    private String nombreIdioma;

    // Optional: inverse side for Libros ManyToOne
    // @OneToMany(mappedBy = "idioma")
    // private Set<Libro> libros;
}
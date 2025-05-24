package com.buhoristeca.lector.model;

import com.buhoristeca.lector.model.Idioma;
import com.buhoristeca.lector.model.Prestamo;
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
@Table(name = "libros")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Long idLibro;

    @Column(nullable = false, length = 255)
    private String titulo;


    @Column(length = 255)
    private String autor;

    @Lob //  potentially long descriptions
    @Column
    private String descripcion;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_editorial", referencedColumnName = "id_editorial", nullable = false)
    private Editorial editorial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_idioma", referencedColumnName = "id_idioma", nullable = false)
    private Idioma idioma;

    @Column(length = 20, unique = true)
    private String isbn;

    @Column(name = "cantidad_ejemplares", nullable = false)
    private Integer cantidadEjemplares;

    @Column(name = "ruta_pdf", length = 500)
    private String rutaPdf;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "libro_genero",
            joinColumns = @JoinColumn(name = "id_libro"),
            inverseJoinColumns = @JoinColumn(name = "id_genero")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Genero> generos = new HashSet<>();

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Prestamo> prestamos = new HashSet<>();
}
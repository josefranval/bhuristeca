package com.buhoristeca.lector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "editoriales")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_editorial")
    private Long idEditorial;

    @Column(name = "nombre_editorial", nullable = false, length = 150, unique = true)
    private String nombreEditorial;

}
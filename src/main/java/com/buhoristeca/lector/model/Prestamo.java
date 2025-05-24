package com.buhoristeca.lector.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "prestamos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Long idPrestamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro", referencedColumnName = "id_libro", nullable = false)
    private Libro libro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_prestamo", nullable = false)
    private LocalDateTime fechaPrestamo;

    @Column(name = "fecha_devolucion_esperada", nullable = false)
    private LocalDate fechaDevolucionEsperada;

    @Column(name = "fecha_devolucion_real")
    private LocalDate fechaDevolucionReal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_prestamo", nullable = false, length = 20)
    private EstadoPrestamo estadoPrestamo; // Enum: PRESTADO, DEVUELTO, VENCIDO
}
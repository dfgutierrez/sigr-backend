package com.sigr.domain.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "placa", unique = true, nullable = false, length = 20)
    private String placa;

    @Column(name = "tipo", nullable = false, length = 10)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Marca marca;

    @Column(name = "modelo", length = 50)
    private String modelo;

    @Column(name = "nombre_conductor", length = 150)
    private String nombreConductor;

    @Column(name = "documento", length = 50)
    private String documento;

    @Column(name = "celular", length = 15)
    private String celular;

    @Column(name = "km")
    private Integer km;

    @Column(name = "sigla", length = 10)
    private String sigla;

    @Column(name = "fecha", columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sede sede;

    @Column(name = "estado", columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean estado = true;

    @PrePersist
    protected void onCreate() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        if (estado == null) {
            estado = true;
        }
    }

}
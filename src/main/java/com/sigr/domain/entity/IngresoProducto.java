package com.sigr.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "ingreso_producto")
public class IngresoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", columnDefinition = "TIMESTAMP DEFAULT now()")
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sede sede;

    @OneToMany(mappedBy = "ingreso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleIngreso> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }

}
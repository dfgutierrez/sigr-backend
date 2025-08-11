package com.sigr.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "venta")
public class Venta {

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

    @Column(name = "fecha_entrega", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaEntrega;

    @Column(name = "total", precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "estado")
    private Boolean estado;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleVenta> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
        if (estado == null) {
            estado = true;
        }
    }

}
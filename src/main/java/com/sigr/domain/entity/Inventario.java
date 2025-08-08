package com.sigr.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Getter
@Setter
@Table(name = "inventario", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"producto_id", "sede_id"}))
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Sede sede;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

}
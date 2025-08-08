package com.sigr.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "ruta", length = 255)
    private String ruta;

    @Column(name = "icono", length = 100)
    private String icono;

    @Column(name = "categoria", length = 100)
    private String categoria;

    @Column(name = "orden")
    private Integer orden;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MenuRol> menuRoles = new ArrayList<>();

}
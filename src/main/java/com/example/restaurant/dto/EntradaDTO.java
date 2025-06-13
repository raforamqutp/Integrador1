package com.example.restaurant.dto;

import java.math.BigDecimal;

public class EntradaDTO {
    private Integer id;
    private String nombre;
    private BigDecimal precio;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    // Getters y Setters
    // Similar a los anteriores pero sin tipo
}

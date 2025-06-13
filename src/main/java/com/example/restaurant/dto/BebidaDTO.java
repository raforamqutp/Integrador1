package com.example.restaurant.dto;

import com.example.restaurant.entidades.TipoBebida;

import java.math.BigDecimal;

public class BebidaDTO {
    private Integer id;
    private String nombre;
    private TipoBebida tipo;
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

    public TipoBebida getTipo() {
        return tipo;
    }

    public void setTipo(TipoBebida tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    // Getters y Setters
    // Similar a ComidaDTO
}
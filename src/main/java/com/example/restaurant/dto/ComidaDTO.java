package com.example.restaurant.dto;

import com.example.restaurant.entidades.TipoComida;

import java.math.BigDecimal;

public class ComidaDTO {
    private Integer id;
    private String nombre;
    private TipoComida tipo;
    private BigDecimal precio;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public TipoComida getTipo() { return tipo; }
    public void setTipo(TipoComida tipo) { this.tipo = tipo; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}

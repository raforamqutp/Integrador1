package com.example.restaurant.dto;

public class ItemDTO {
    private String nombre;     // Campo presente en ambas versiones
    private String categoria;  // Campo presente en ambas versiones
    private int cantidad;     // Campo presente en ambas versiones
    private double precio;    // Campo adicional de la primera versión

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
package com.example.restaurant.entidades;

import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "Entrada")
public class Entrada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_entrada")
    private Integer idEntrada;

    @Column(name = "nombre_entrada", nullable = false)
    private String nombreEntrada;

    @Column(name = "precio", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal precio;

    // Getters y Setters
    public Integer getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(Integer idEntrada) {
        this.idEntrada = idEntrada;
    }

    public String getNombreEntrada() {
        return nombreEntrada;
    }

    public void setNombreEntrada(String nombreEntrada) {
        this.nombreEntrada = nombreEntrada;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
}

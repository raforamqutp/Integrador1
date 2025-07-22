package com.example.restaurant.entidades;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
// ✅ Se quita la anotación @Data para definir los métodos manualmente
@Table(name = "historial_ventas_archivado")
public class HistorialVentaArchivado {

    @Id
    private Integer idPedido; // Usamos el ID del pedido original como PK

    private LocalDateTime fecha;

    private String nombreCliente;

    private String nombreUsuario;

    private BigDecimal total;

    @Lob // Indica que es un Large Object
    @Column(columnDefinition = "TEXT") // Mapea a un tipo de dato TEXT en la BD
    private String detallesJson;

    // --- Getters y Setters ---

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDetallesJson() {
        return detallesJson;
    }

    public void setDetallesJson(String detallesJson) {
        this.detallesJson = detallesJson;
    }

    // --- Métodos de utilidad (opcionales pero recomendados) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorialVentaArchivado that = (HistorialVentaArchivado) o;
        return Objects.equals(idPedido, that.idPedido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPedido);
    }
}
package com.example.restaurant.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Pension")
public class Pension {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pension")
    private Integer idPension;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "monto_mensual", nullable = false)
    private Double montoMensual;

	public Integer getIdPension() {
		return idPension;
	}

	public void setIdPension(Integer idPension) {
		this.idPension = idPension;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Double getMontoMensual() {
		return montoMensual;
	}

	public void setMontoMensual(Double montoMensual) {
		this.montoMensual = montoMensual;
	}


    
}

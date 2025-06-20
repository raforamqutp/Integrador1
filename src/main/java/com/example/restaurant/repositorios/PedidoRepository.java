package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Pedido;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
	List<Pedido> findByUsuario_IdUsuario(Integer usuarioId);
}


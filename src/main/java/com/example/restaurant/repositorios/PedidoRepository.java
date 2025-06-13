// src/main/java/com/example/restaurant/repositorios/PedidoRepository.java
package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.entidades.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.restaurant.entidades.Cliente;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
	List<Pedido> findByUsuario_IdUsuario(Integer usuarioId);
	// List<DetallePedido> findByPedido(Pedido pedido); // <-- Elimina o comenta esta línea
	Optional<Pedido> findById(Long id);
	List<Pedido> findByCliente(Cliente cliente);
}
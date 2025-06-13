// src/main/java/com/example/restaurant/repositorios/DetallePedidoRepository.java
package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.entidades.Comida;
import com.example.restaurant.entidades.Bebida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedido(Pedido pedido);
    List<DetallePedido> findByPedidoId(Long pedidoId);

    // Si DetallePedido tiene un campo Comida:
    Optional<DetallePedido> findByPedidoAndComida(Pedido pedido, Comida comida);

    // Si DetallePedido tiene un campo Bebida:
    Optional<DetallePedido> findByPedidoAndBebida(Pedido pedido, Bebida bebida);
}
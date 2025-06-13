package com.example.restaurant.interfaces;

import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.entidades.Comida;
import com.example.restaurant.entidades.Bebida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IDetallePedido extends JpaRepository<DetallePedido, Long> {
    List<DetallePedido> findByPedido(Pedido pedido);
    List<DetallePedido> findByPedidoId(Long pedidoId);
    Optional<DetallePedido> findByPedidoAndComida(Pedido pedido, Comida comida);
    Optional<DetallePedido> findByPedidoAndBebida(Pedido pedido, Bebida bebida);
}
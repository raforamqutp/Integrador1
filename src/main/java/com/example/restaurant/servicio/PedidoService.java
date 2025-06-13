// src/main/java/com/example/restaurant/servicio/PedidoService.java
package com.example.restaurant.servicio;

import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.repositorios.PedidoRepository;
import com.example.restaurant.repositorios.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    private Pedido pedidoActual;

    public Pedido guardarPedido(Pedido pedido) {
        pedidoActual = pedidoRepository.save(pedido);
        return pedidoActual;
    }

    public void agregarDetallePedido(DetallePedido detalle) {
        if (pedidoActual != null) {
            detalle.setPedido(pedidoActual);
            detallePedidoRepository.save(detalle);
        }
    }

    public Pedido SobtenerPedidoActual() {
        return pedidoActual;
    }

    public void eliminarDetalle(Long id) {
        detallePedidoRepository.deleteById(id);
    }

    public void finalizarPedidoActual() {
        if (pedidoActual != null) {
            pedidoActual = null; // Finaliza el pedido actual
        }
    }
}
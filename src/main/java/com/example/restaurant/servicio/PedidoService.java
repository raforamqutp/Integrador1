package com.example.restaurant.servicio;

import com.example.restaurant.model.Item;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final List<Item> pedido = new ArrayList<>();

    public void agregarItem(Item item) {
        // Si ya existe el ítem, suma la cantidad
        for (Item existente : pedido) {
            if (existente.getNombre().equalsIgnoreCase(item.getNombre())) {
                existente.setCantidad(existente.getCantidad() + item.getCantidad());
                return;
            }
        }
        pedido.add(item);
    }

    public List<Item> obtenerPedido() {
        return pedido;
    }

    public void limpiarPedido() {
        pedido.clear();
    }
}
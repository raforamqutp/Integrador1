package com.example.restaurant.interfaceService;

import com.example.restaurant.entidades.Pedido;
import java.util.List;
import java.util.Optional;

public interface IPedidoService {
    List<Pedido> listar();
    Optional<Pedido> listarId(Long id);
    int save(Pedido pedido);
    void delete(Long id);
}
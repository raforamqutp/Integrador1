package com.example.restaurant.interfaces;

import com.example.restaurant.entidades.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPedido extends JpaRepository<Pedido, Long> {
}
package com.example.restaurant.interfaces;

import com.example.restaurant.entidades.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICliente extends JpaRepository<Cliente, Integer> {
}
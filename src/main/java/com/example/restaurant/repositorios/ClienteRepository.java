package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Cliente;
import com.example.restaurant.entidades.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    
    // Buscar por nombre (case insensitive)
    List<Cliente> findByNombreClienteContainingIgnoreCase(String nombre);
    
    // Buscar por tipo de cliente
    List<Cliente> findByTipoCliente(TipoCliente tipoCliente);
}
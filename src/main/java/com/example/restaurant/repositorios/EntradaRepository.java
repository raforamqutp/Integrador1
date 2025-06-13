package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Entrada;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradaRepository extends JpaRepository<Entrada, Integer> {
    Entrada findByNombreEntrada(String nombreEntrada);
}

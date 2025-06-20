package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Bebida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BebidaRepository extends JpaRepository<Bebida, Integer> {
    Bebida findByNombreBebida(String nombreBebida);
}

package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Comida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComidaRepository extends JpaRepository<Comida, Integer> {
    Comida findByNombreComida(String nombreComida);
}

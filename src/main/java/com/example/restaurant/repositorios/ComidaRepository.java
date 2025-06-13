package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.Comida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComidaRepository extends JpaRepository<Comida, Long> {
    Comida findByNombreComida(String nombreComida);
}
package com.example.restaurant.interfaces;

import com.example.restaurant.entidades.Bebida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBebida extends JpaRepository<Bebida, Integer> {
    Bebida findByNombreBebida(String nombre);
}
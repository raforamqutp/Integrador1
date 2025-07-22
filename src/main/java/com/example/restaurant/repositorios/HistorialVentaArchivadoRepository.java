package com.example.restaurant.repositorios;

import com.example.restaurant.entidades.HistorialVentaArchivado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialVentaArchivadoRepository extends JpaRepository<HistorialVentaArchivado, Integer> {
}
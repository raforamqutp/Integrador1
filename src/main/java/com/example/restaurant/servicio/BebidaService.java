package com.example.restaurant.servicio;

import com.example.restaurant.entidades.Bebida;
import com.example.restaurant.repositorios.BebidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BebidaService {
    @Autowired
    private BebidaRepository bebidaRepository;

    public List<Bebida> listarBebidas() {
        return bebidaRepository.findAll();
    }

    public Bebida guardarBebida(Bebida bebida) {
        return bebidaRepository.save(bebida);
    }

    public Bebida buscarPorId(Integer id) {
        return bebidaRepository.findById(id).orElse(null);
    }

    public void eliminarBebida(Integer id) {
        bebidaRepository.deleteById(id);
    }
}
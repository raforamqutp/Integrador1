package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Bebida;
import com.example.restaurant.entidades.Comida;
import com.example.restaurant.entidades.TipoComida;
import com.example.restaurant.repositorios.BebidaRepository;
import com.example.restaurant.repositorios.ComidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PlatosController {

    private final ComidaRepository comidaRepository;
    private final BebidaRepository bebidaRepository;

    @Autowired
    public PlatosController(ComidaRepository comidaRepository, BebidaRepository bebidaRepository) {
        this.comidaRepository = comidaRepository;
        this.bebidaRepository = bebidaRepository;
    }

    @GetMapping("/platos") // Esta será la nueva URL para acceder a la página
    public String mostrarPaginaPlatos(Model model) {
        // Obtenemos todos los datos del menú desde la base de datos
        List<Comida> platos = comidaRepository.findByTipoComida(TipoComida.plato);
        List<Comida> entradas = comidaRepository.findByTipoComida(TipoComida.entrada);
        List<Bebida> bebidas = bebidaRepository.findAll();

        // Los añadimos al modelo para que Thymeleaf pueda usarlos
        model.addAttribute("platos", platos);
        model.addAttribute("entradas", entradas);
        model.addAttribute("bebidas", bebidas);

        // Devolvemos el nombre de la plantilla HTML (sin la extensión .html)
        return "platos";
    }
}
package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Comida;
import com.example.restaurant.entidades.Bebida;
import com.example.restaurant.servicio.ComidaService;
import com.example.restaurant.servicio.BebidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductController {
    @Autowired
    private ComidaService comidaService;

    @Autowired
    private BebidaService bebidaService;

    @GetMapping("/lista")
    public String listarProductos(Model model) {
        List<Comida> comidas = comidaService.listarComidas();
        List<Bebida> bebidas = bebidaService.listarBebidas();

        model.addAttribute("comidas", comidas);
        model.addAttribute("bebidas", bebidas);
        return "productos";
    }

    @GetMapping("/platos")
    public String mostrarPlatos(Model model) {
        List<Comida> comidas = comidaService.listarComidas();
        model.addAttribute("comidas", comidas);
        return "platos";
    }

    @GetMapping("/bebidas")
    public String mostrarBebidas(Model model) {
        List<Bebida> bebidas = bebidaService.listarBebidas();
        model.addAttribute("bebidas", bebidas);
        return "bebidas";
    }
}
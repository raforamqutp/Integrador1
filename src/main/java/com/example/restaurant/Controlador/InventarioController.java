package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Insumo;
import com.example.restaurant.repositorios.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/inventario") // Todas las operaciones se manejarán bajo esta ruta
public class InventarioController {

    private final InsumoRepository insumoRepository;

    @Autowired
    public InventarioController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    // Muestra la página unificada de gestión de insumos
    @GetMapping
    public String mostrarGestionDeInsumos(Model model) {
        List<Insumo> insumos = insumoRepository.findAll();
        model.addAttribute("insumos", insumos);
        // ✅ Apunta al nuevo archivo unificado
        return "inventario";
    }

    // Actualiza el stock de un insumo existente
    @PostMapping("/actualizar")
    public String actualizarStock(@RequestParam("id") Integer id,
                                  @RequestParam("cantidad") Integer cantidad,
                                  RedirectAttributes redirectAttributes) {
        // Validación para evitar stock negativo
        if (cantidad < 0) {
            redirectAttributes.addFlashAttribute("error", "El stock no puede ser un número negativo.");
            return "redirect:/inventario";
        }

        return insumoRepository.findById(id)
                .map(insumo -> {
                    insumo.setStock(cantidad);
                    insumoRepository.save(insumo);
                    redirectAttributes.addFlashAttribute("success", "Stock actualizado correctamente.");
                    return "redirect:/inventario";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Insumo no encontrado.");
                    return "redirect:/inventario";
                });
    }

    // Agrega un nuevo insumo a la base de datos
    @PostMapping("/agregar")
    public String agregarInsumo(@RequestParam("nombre") String nombre,
                                @RequestParam("stock") Integer stock,
                                RedirectAttributes redirectAttributes) {
        if (nombre == null || nombre.trim().isEmpty() || stock == null || stock < 0) {
            redirectAttributes.addFlashAttribute("error", "Los datos para agregar el insumo son inválidos.");
            return "redirect:/inventario"; // ✅ Redirige a la página unificada
        }

        Insumo nuevo = new Insumo();
        nuevo.setNombreInsumo(nombre.trim());
        nuevo.setStock(stock);
        insumoRepository.save(nuevo);

        redirectAttributes.addFlashAttribute("success", "Insumo agregado correctamente.");
        return "redirect:/inventario"; // ✅ Redirige a la página unificada
    }

    // Elimina un insumo de la base de datos
    @PostMapping("/eliminar")
    public String eliminarInsumo(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (insumoRepository.existsById(id)) {
            insumoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Insumo eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "El insumo que intenta eliminar no existe.");
        }
        return "redirect:/inventario"; // ✅ Redirige a la página unificada
    }
}
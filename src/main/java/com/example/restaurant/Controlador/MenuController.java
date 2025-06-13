package com.example.restaurant.Controlador;

import com.example.restaurant.dto.*;
import com.example.restaurant.entidades.*;
import com.example.restaurant.servicio.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // Mostrar vista principal con pestañas
    @GetMapping("")
    public String mostrarMenu(@RequestParam(value = "tab", defaultValue = "comidas") String tab, Model model) {
        model.addAttribute("tab", tab);

        switch (tab) {
            case "comidas":
                model.addAttribute("comidas", menuService.listarComidas());
                break;
            case "bebidas":
                model.addAttribute("bebidas", menuService.listarBebidas());
                break;
            case "entradas":
                model.addAttribute("entradas", menuService.listarEntradas());
                break;
        }

        return "menu-crud";
    }

    // ========== CRUD PARA COMIDAS ==========
    @GetMapping("/comida/nuevo")
    public String mostrarFormularioComida(Model model) {
        model.addAttribute("comida", new ComidaDTO());
        model.addAttribute("tiposComida", TipoComida.values());
        return "form-comida";
    }

    @PostMapping("/comida/guardar")
    public String guardarComida(@ModelAttribute ComidaDTO comidaDTO, RedirectAttributes redirectAttributes) {
        try {
            // Validación básica del precio
            if (comidaDTO.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor que cero");
            }

            menuService.guardarComida(comidaDTO);
            redirectAttributes.addFlashAttribute("message", "Plato guardado exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al guardar el plato: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/menu?tab=comidas";
    }

    @GetMapping("/comida/editar/{id}")
    public String editarComida(@PathVariable Integer id, Model model) {
        ComidaDTO comidaDTO = menuService.buscarComidaPorId(id);
        if (comidaDTO == null) {
            return "redirect:/admin/menu?tab=comidas";
        }
        model.addAttribute("comida", comidaDTO);
        model.addAttribute("tiposComida", TipoComida.values());
        return "form-comida";
    }

    @PostMapping("/comida/eliminar/{id}")
    public String eliminarComida(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            menuService.eliminarComida(id);
            redirectAttributes.addFlashAttribute("message", "Plato eliminado exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al eliminar el plato: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/menu?tab=comidas";
    }

    @GetMapping("/comida/buscar")
    public String buscarComidas(@RequestParam String query, Model model) {
        model.addAttribute("comidas", menuService.buscarComidasPorNombre(query));
        model.addAttribute("tab", "comidas");
        return "menu-crud";
    }

    // ========== CRUD PARA BEBIDAS ==========
    @GetMapping("/bebida/nuevo")
    public String mostrarFormularioBebida(Model model) {
        model.addAttribute("bebida", new BebidaDTO());
        model.addAttribute("tiposBebida", TipoBebida.values());
        return "form-bebida";
    }

    @PostMapping("/bebida/guardar")
    public String guardarBebida(@ModelAttribute BebidaDTO bebidaDTO, RedirectAttributes redirectAttributes) {
        try {
            if (bebidaDTO.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor que cero");
            }

            menuService.guardarBebida(bebidaDTO);
            redirectAttributes.addFlashAttribute("message", "Bebida guardada exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al guardar la bebida: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/menu?tab=bebidas";
    }

    @GetMapping("/bebida/editar/{id}")
    public String editarBebida(@PathVariable Integer id, Model model) {
        BebidaDTO bebidaDTO = menuService.buscarBebidaPorId(id);
        if (bebidaDTO == null) {
            return "redirect:/admin/menu?tab=bebidas";
        }
        model.addAttribute("bebida", bebidaDTO);
        model.addAttribute("tiposBebida", TipoBebida.values());
        return "form-bebida";
    }

    @PostMapping("/bebida/eliminar/{id}")
    public String eliminarBebida(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            menuService.eliminarBebida(id);
            redirectAttributes.addFlashAttribute("message", "Bebida eliminada exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al eliminar la bebida: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/menu?tab=bebidas";
    }

    @GetMapping("/bebida/buscar")
    public String buscarBebidas(@RequestParam String query, Model model) {
        model.addAttribute("bebidas", menuService.buscarBebidasPorNombre(query));
        model.addAttribute("tab", "bebidas");
        return "menu-crud";
    }

    // ========== CRUD PARA ENTRADAS ==========
    @GetMapping("/entrada/nuevo")
    public String mostrarFormularioEntrada(Model model) {
        model.addAttribute("entrada", new EntradaDTO());
        return "form-entrada";
    }

    @PostMapping("/entrada/guardar")
    public String guardarEntrada(@ModelAttribute EntradaDTO entradaDTO, RedirectAttributes redirectAttributes) {
        try {
            if (entradaDTO.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor que cero");
            }

            menuService.guardarEntrada(entradaDTO);
            redirectAttributes.addFlashAttribute("message", "Entrada guardada exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al guardar la entrada: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/menu?tab=entradas";
    }

    @GetMapping("/entrada/editar/{id}")
    public String editarEntrada(@PathVariable Integer id, Model model) {
        EntradaDTO entradaDTO = menuService.buscarEntradaPorId(id);
        if (entradaDTO == null) {
            return "redirect:/admin/menu?tab=entradas";
        }
        model.addAttribute("entrada", entradaDTO);
        return "form-entrada";
    }

    @PostMapping("/entrada/eliminar/{id}")
    public String eliminarEntrada(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            menuService.eliminarEntrada(id);
            redirectAttributes.addFlashAttribute("message", "Entrada eliminada exitosamente");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al eliminar la entrada: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        return "redirect:/admin/menu?tab=entradas";
    }

    @GetMapping("/entrada/buscar")
    public String buscarEntradas(@RequestParam String query, Model model) {
        model.addAttribute("entradas", menuService.buscarEntradasPorNombre(query));
        model.addAttribute("tab", "entradas");
        return "menu-crud";
    }
}

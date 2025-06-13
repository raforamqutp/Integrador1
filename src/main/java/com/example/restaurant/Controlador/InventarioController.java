// V3
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
@RequestMapping("/inventario")
public class InventarioController {

    private final InsumoRepository insumoRepository;

    @Autowired
    public InventarioController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @GetMapping
    public String mostrarInventario(Model model) {
        List<Insumo> insumos = insumoRepository.findAll();
        model.addAttribute("insumos", insumos);
        return "inventario";
    }

    @PostMapping("/actualizar")
    public String actualizarStock(@RequestParam("id") Integer id,
                                 @RequestParam("cantidad") Integer cantidad,
                                 RedirectAttributes redirectAttributes) {
        Insumo insumo = insumoRepository.findById(id).orElse(null);
        
        if (insumo != null) {
            insumo.setStock(cantidad);
            insumoRepository.save(insumo);
            redirectAttributes.addFlashAttribute("success", "Stock actualizado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Insumo no encontrado");
        }
        
        return "redirect:/inventario";
    }
    
    @PostMapping("/agregar")
    public String agregarInsumo(@RequestParam("nombre") String nombre,
                                @RequestParam("stock") Integer stock,
                                RedirectAttributes redirectAttributes) {
        if (nombre == null || nombre.trim().isEmpty() || stock == null || stock < 0) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos");
            return "redirect:/compras";
        }

        Insumo nuevo = new Insumo();
        nuevo.setNombreInsumo(nombre.trim());
        nuevo.setStock(stock);
        insumoRepository.save(nuevo);

        redirectAttributes.addFlashAttribute("success", "Insumo agregado correctamente");
        return "redirect:/compras";
    }
    
    @PostMapping("/eliminar")
    public String eliminarInsumo(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        if (insumoRepository.existsById(id)) {
            insumoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Insumo eliminado correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "El insumo no existe");
        }
        return "redirect:/compras";
    }
}


/*
V2
package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.Insumo;
import com.example.restaurant.repositorios.InsumoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/inventario")
public class InventarioController {

    private final InsumoRepository insumoRepository;

    @Autowired
    public InventarioController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    @GetMapping
    public String mostrarInventario(Model model, 
                                   @RequestParam(required = false) String nombre) {
        List<Insumo> insumos;
        
        if (nombre != null && !nombre.isEmpty()) {
            insumos = insumoRepository.findByNombreInsumoContainingIgnoreCase(nombre);
        } else {
            insumos = insumoRepository.findAll();
        }
        
        model.addAttribute("insumos", insumos);
        return "inventario"; // Nombre del archivo HTML sin extensión
    }
    
    
}
*/

/*
V1


package com.example.restaurant.Controlador;

import com.example.restaurant.dto.StockResponseDTO;
import com.example.restaurant.entidades.Insumo;
import com.example.restaurant.servicio.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    @Autowired
    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping("/stock")
    public ResponseEntity<?> consultarStock(
            @RequestParam(required = false) String nombre) {
        
        if (nombre != null && !nombre.isEmpty()) {
            List<Insumo> insumos = inventarioService.buscarInsumosPorNombre(nombre);
            List<StockResponseDTO> response = insumos.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body("Se requiere el parámetro 'nombre'");
        }
    }

    @GetMapping("/stock/{id}")
    public ResponseEntity<StockResponseDTO> consultarStockPorId(
            @PathVariable Integer id) {
        
        Optional<Insumo> insumo = inventarioService.obtenerInsumoPorId(id);
        return insumo.map(value -> ResponseEntity.ok(convertToDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private StockResponseDTO convertToDTO(Insumo insumo) {
        StockResponseDTO dto = new StockResponseDTO();
        dto.setIdInsumo(insumo.getIdInsumo());
        dto.setNombreInsumo(insumo.getNombreInsumo());
        dto.setStock(insumo.getStock());
        return dto;
    }
}

 */
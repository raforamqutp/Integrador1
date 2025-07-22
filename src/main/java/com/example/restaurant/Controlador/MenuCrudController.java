package com.example.restaurant.Controlador;

import com.example.restaurant.entidades.*;
import com.example.restaurant.repositorios.BebidaRepository;
import com.example.restaurant.repositorios.ComidaRepository;
import com.example.restaurant.repositorios.PedidoRepository; // ✅ 1. Importar PedidoRepository
import com.example.restaurant.servicio.StorageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/menu-crud")
public class MenuCrudController {

    private final ComidaRepository comidaRepository;
    private final BebidaRepository bebidaRepository;
    private final StorageService storageService;
    private final PedidoRepository pedidoRepository; // ✅ 2. Añadir el repositorio

    @Autowired
    public MenuCrudController(ComidaRepository comidaRepository, BebidaRepository bebidaRepository, StorageService storageService, PedidoRepository pedidoRepository) { // ✅ 3. Añadir al constructor
        this.comidaRepository = comidaRepository;
        this.bebidaRepository = bebidaRepository;
        this.storageService = storageService;
        this.pedidoRepository = pedidoRepository; // ✅ 4. Asignar en el constructor
    }

    /**
     * Verifica si la caja está abierta (si hay pedidos en el historial activo).
     * @return true si hay al menos un pedido, false en caso contrario.
     */
    private boolean isCajaAbierta() {
        return pedidoRepository.count() > 0;
    }

    @GetMapping
    public String mostrarMenu(@RequestParam(value = "tab", required = false, defaultValue = "comidas") String tab, Model model) {
        model.addAttribute("tab", tab);
        model.addAttribute("cajaAbierta", isCajaAbierta()); // ✅ 5. Pasar estado de la caja a la vista

        if ("bebidas".equals(tab)) {
            model.addAttribute("bebidas", bebidaRepository.findAll());
            model.addAttribute("comidas", Collections.emptyList());
        } else {
            List<Comida> comidasList = "entradas".equals(tab)
                    ? comidaRepository.findByTipoComida(TipoComida.entrada)
                    : comidaRepository.findByTipoComida(TipoComida.plato);
            model.addAttribute("comidas", comidasList);
            model.addAttribute("bebidas", Collections.emptyList());
        }
        return "menu-crud";
    }

    @PostMapping("/guardarComida")
    @Transactional
    public String guardarComida(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") String precioStr,
            @RequestParam("tipoComida") String tipoComidaStr,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        // ✅ 6. Añadir protección
        if (isCajaAbierta()) {
            redirectAttributes.addFlashAttribute("message", "No se puede modificar el menú mientras la caja está abierta.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/menu-crud?tab=" + ("entrada".equals(tipoComidaStr) ? "entradas" : "comidas");
        }

        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío.");
            }
            BigDecimal precio = new BigDecimal(precioStr.replace(',', '.'));
            TipoComida tipoComida = TipoComida.valueOf(tipoComidaStr);
            Comida comida = (id != null && id > 0)
                    ? comidaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comida no encontrada"))
                    : new Comida();
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String nombreArchivo = storageService.store(imagenFile);
                comida.setImagen(nombreArchivo);
            }
            comida.setNombreComida(nombre);
            comida.setPrecio(precio);
            comida.setTipoComida(tipoComida);
            comidaRepository.save(comida);
            redirectAttributes.addFlashAttribute("message", "Comida guardada exitosamente.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        String tabRedirect = "entrada".equals(tipoComidaStr) ? "entradas" : "comidas";
        return "redirect:/menu-crud?tab=" + tabRedirect;
    }

    @PostMapping("/guardarBebida")
    @Transactional
    public String guardarBebida(
            @RequestParam(name = "id", required = false) Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") String precioStr,
            @RequestParam("tipoBebida") String tipoBebidaStr,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {

        // ✅ 7. Añadir protección
        if (isCajaAbierta()) {
            redirectAttributes.addFlashAttribute("message", "No se puede modificar el menú mientras la caja está abierta.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/menu-crud?tab=bebidas";
        }

        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío.");
            }
            BigDecimal precio = new BigDecimal(precioStr.replace(',', '.'));
            TipoBebida tipoBebida = TipoBebida.valueOf(tipoBebidaStr);
            Bebida bebida = (id != null && id > 0)
                    ? bebidaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bebida no encontrada"))
                    : new Bebida();
            if (imagenFile != null && !imagenFile.isEmpty()) {
                String nombreArchivo = storageService.store(imagenFile);
                bebida.setImagen(nombreArchivo);
            }
            bebida.setNombreBebida(nombre);
            bebida.setPrecio(precio);
            bebida.setTipoBebida(tipoBebida);
            bebidaRepository.save(bebida);
            redirectAttributes.addFlashAttribute("message", "Bebida guardada exitosamente.");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Error al guardar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }
        return "redirect:/menu-crud?tab=bebidas";
    }

    @PostMapping("/comida/eliminar/{id}")
    public String eliminarComida(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) { // ✅ 8. Añadir RedirectAttributes
        // ✅ 9. Añadir protección
        if (isCajaAbierta()) {
            redirectAttributes.addFlashAttribute("message", "No se puede modificar el menú mientras la caja está abierta.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            // Redirigimos a la última pestaña conocida (asumimos comidas si no se sabe)
            return "redirect:/menu-crud?tab=comidas";
        }
        comidaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Elemento eliminado correctamente.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/menu-crud?tab=comidas";
    }

    @PostMapping("/bebida/eliminar/{id}")
    public String eliminarBebida(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) { // ✅ 10. Añadir RedirectAttributes
        // ✅ 11. Añadir protección
        if (isCajaAbierta()) {
            redirectAttributes.addFlashAttribute("message", "No se puede modificar el menú mientras la caja está abierta.");
            redirectAttributes.addFlashAttribute("messageType", "danger");
            return "redirect:/menu-crud?tab=bebidas";
        }
        bebidaRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Elemento eliminado correctamente.");
        redirectAttributes.addFlashAttribute("messageType", "success");
        return "redirect:/menu-crud?tab=bebidas";
    }

    // === OBTENER DATOS PARA EDITAR (sin cambios) ===
    @GetMapping("/comida/editar/{id}")
    @ResponseBody
    public Comida obtenerComida(@PathVariable("id") Integer id) {
        return comidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comida no encontrada con ID: " + id));
    }

    @GetMapping("/bebida/editar/{id}")
    @ResponseBody
    public Bebida obtenerBebida(@PathVariable("id") Integer id) {
        return bebidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bebida no encontrada con ID: " + id));
    }
}

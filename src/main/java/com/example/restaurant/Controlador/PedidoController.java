package com.example.restaurant.controlador;

import com.example.restaurant.dto.ItemDTO;
import com.example.restaurant.modelo.Item;
import com.example.restaurant.servicio.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedido")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping("/agregar")
    public void agregarItem(@RequestBody ItemDTO dto) {
        // Convertir categoría a mayúsculas para coincidir con el ENUM de la BD
        Item item = new Item(dto.getNombre(), dto.getCategoria().toUpperCase(), dto.getCantidad());
        pedidoService.agregarItem(item);
    }

    @GetMapping("/ver")
    public List<Item> verPedido() {
        return pedidoService.obtenerPedido();
    }

    @DeleteMapping("/limpiar")
    public void limpiarPedido() {
        pedidoService.limpiarPedido();
    }
}



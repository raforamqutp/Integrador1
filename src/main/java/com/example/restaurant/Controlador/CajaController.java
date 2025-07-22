package com.example.restaurant.Controlador;

import com.example.restaurant.repositorios.PedidoRepository;
import com.example.restaurant.servicio.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/caja")
public class CajaController {

    @Autowired
    private CajaService cajaService;

    @Autowired
    private PedidoRepository pedidoRepository; // Inyectamos el repositorio de pedidos

    @GetMapping("/estado")
    public ResponseEntity<Map<String, String>> obtenerEstadoCaja() {
        long conteoPedidos = pedidoRepository.count();
        String estado = (conteoPedidos > 0) ? "abierta" : "cerrada";
        return ResponseEntity.ok(Map.of("estado", estado));
    }

    @PostMapping("/cerrar")
    public ResponseEntity<?> cerrarCaja() {
        try {
            cajaService.cerrarCaja();
            return ResponseEntity.ok(Map.of("message", "Caja cerrada y historial archivado con éxito."));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Ocurrió un error inesperado al cerrar la caja."));
        }
    }

}
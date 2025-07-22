package com.example.restaurant.servicio;

import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.entidades.HistorialVentaArchivado;
import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.repositorios.DetallePedidoRepository; // ✅ Se usa el repositorio que ya existe
import com.example.restaurant.repositorios.HistorialVentaArchivadoRepository;
import com.example.restaurant.repositorios.PedidoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CajaService {

    private final PedidoRepository pedidoRepository;
    // ✅ CORRECCIÓN: Se inyecta el único repositorio de detalles que existe.
    private final DetallePedidoRepository detallePedidoRepository;
    private final HistorialVentaArchivadoRepository historialArchivadoRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public CajaService(PedidoRepository pedidoRepository,
                       DetallePedidoRepository detallePedidoRepository,
                       HistorialVentaArchivadoRepository historialArchivadoRepository,
                       ObjectMapper objectMapper) {
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.historialArchivadoRepository = historialArchivadoRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void cerrarCaja() {
        List<Pedido> pedidosActivos = pedidoRepository.findAll();
        if (pedidosActivos.isEmpty()) {
            throw new IllegalStateException("No hay pedidos en el historial para archivar.");
        }

        List<HistorialVentaArchivado> archivos = new ArrayList<>();
        for (Pedido pedido : pedidosActivos) {
            HistorialVentaArchivado archivo = new HistorialVentaArchivado();

            // ✅ CORRECCIÓN: Se usa el nombre de método correcto para el ID del pedido.
            archivo.setIdPedido(pedido.getIdPedido());
            archivo.setFecha(pedido.getFecha());
            archivo.setNombreCliente(pedido.getCliente().getNombreCompleto());
            archivo.setNombreUsuario(pedido.getUsuario().getNombreUsuario());
            archivo.setTotal(pedido.getTotal());

            // ✅ CORRECCIÓN: Se procesa una única lista de detalles.
            // Se asume que la entidad Pedido tiene un método getDetalles() que devuelve List<DetallePedido>
            Map<String, Object> detallesMap = new HashMap<>();
            List<Map<String, Object>> detallesComidaList = new ArrayList<>();
            List<Map<String, Object>> detallesBebidaList = new ArrayList<>();

            for (DetallePedido detalle : pedido.getDetalles()) {
                if (detalle.getComida() != null) { // Si el detalle tiene una comida asociada
                    detallesComidaList.add(Map.of(
                            "nombre", detalle.getComida().getNombreComida(),
                            "cantidad", detalle.getCantidad(),
                            "precio", detalle.getPrecioUnitario()
                    ));
                } else if (detalle.getBebida() != null) { // Si el detalle tiene una bebida asociada
                    detallesBebidaList.add(Map.of(
                            "nombre", detalle.getBebida().getNombreBebida(),
                            "cantidad", detalle.getCantidad(),
                            "precio", detalle.getPrecioUnitario()
                    ));
                }
            }

            detallesMap.put("comidas", detallesComidaList);
            detallesMap.put("bebidas", detallesBebidaList);

            try {
                archivo.setDetallesJson(objectMapper.writeValueAsString(detallesMap));
            } catch (JsonProcessingException e) {
                // ✅ CORRECCIÓN: Se usa el nombre de método correcto para el ID del pedido.
                throw new RuntimeException("Error al serializar detalles del pedido " + pedido.getIdPedido(), e);
            }
            archivos.add(archivo);
        }

        // 1. Guardar todos los registros en la tabla de archivo
        historialArchivadoRepository.saveAll(archivos);

        // 2. Eliminar los registros originales de forma segura
        // ✅ CORRECCIÓN: Se usa el único repositorio para borrar todos los detalles.
        detallePedidoRepository.deleteAllInBatch();
        pedidoRepository.deleteAllInBatch();
    }
}
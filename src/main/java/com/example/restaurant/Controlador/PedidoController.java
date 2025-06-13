package com.example.restaurant.Controlador; // Corregido para coincidir con la estructura de carpetas

import com.example.restaurant.dto.PedidoDTO;
import com.example.restaurant.dto.ItemDTO;
import com.example.restaurant.entidades.Pedido;
import com.example.restaurant.entidades.DetallePedido;
import com.example.restaurant.entidades.TipoItem;
import com.example.restaurant.entidades.Comida;
import com.example.restaurant.entidades.Bebida;
import com.example.restaurant.generador.PedidoExcelGenerador;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedido")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    // Dentro de PedidoController.java


    private Pedido convertirDTOAEntidad(PedidoDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setFechaPedido(LocalDateTime.now()); // Considerar si esta fecha debe venir del DTO o ser manejada por un servicio

        List<DetallePedido> detalles = new ArrayList<>();

        if (pedidoDTO.getPlatos() != null) {
            pedidoDTO.getPlatos().forEach((nombreItem, itemDTO) ->
                    procesarItem(detalles, pedido, nombreItem, itemDTO, TipoItem.COMIDA)
            );
        }

        if (pedidoDTO.getBebidas() != null) {
            pedidoDTO.getBebidas().forEach((nombreItem, itemDTO) ->
                    procesarItem(detalles, pedido, nombreItem, itemDTO, TipoItem.BEBIDA)
            );
        }

        pedido.setDetalles(detalles);
        // Calcular el total del pedido si es necesario aquí o en la entidad Pedido
        // BigDecimal totalPedido = detalles.stream().map(DetallePedido::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        // pedido.setTotal(totalPedido);
        return pedido;
    }

    private void procesarItem(List<DetallePedido> detalles, Pedido pedido, String nombreItem, ItemDTO itemDTO, TipoItem tipo) {
        if (itemDTO.getCantidad() > 0) {
            DetallePedido detalle = new DetallePedido();
            BigDecimal precioUnitario = BigDecimal.valueOf(itemDTO.getPrecio());
            BigDecimal cantidad = BigDecimal.valueOf(itemDTO.getCantidad());
            BigDecimal subtotal = precioUnitario.multiply(cantidad);

            if (tipo == TipoItem.COMIDA) {
                Comida comida = new Comida();
                comida.setNombreComida(nombreItem);
                comida.setPrecio(precioUnitario);
                // Si Comida es una entidad JPA, podría necesitar ser guardada o referenciada de otra forma
                detalle.setComida(comida);
            } else if (tipo == TipoItem.BEBIDA) {
                Bebida bebida = new Bebida();
                bebida.setNombreBebida(nombreItem);
                bebida.setPrecio(precioUnitario);
                // Si Bebida es una entidad JPA, podría necesitar ser guardada o referenciada de otra forma
                detalle.setBebida(bebida);
            }

            detalle.setTipoItem(tipo);
            detalle.setCantidad(itemDTO.getCantidad());
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            detalle.setPedido(pedido);

            detalles.add(detalle);
        }
    }
}
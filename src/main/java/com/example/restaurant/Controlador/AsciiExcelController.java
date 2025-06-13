package com.example.restaurant.Controlador;

import com.example.restaurant.dto.PedidoDTO;
import com.example.restaurant.generador.PedidoExcelGenerador;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/pedido")
@CrossOrigin(origins = "http://localhost:63342") // <--- ¡AÑADE ESTA LÍNEA!
public class AsciiExcelController {

    private static final Logger logger = LoggerFactory.getLogger(AsciiExcelController.class);

    @PostMapping(value = "/generar-excel", consumes = "application/json")
    public ResponseEntity<byte[]> generarExcelDesdePedido(@RequestBody PedidoDTO pedido) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PedidoExcelGenerador.generarExcel(pedido, out);

            HttpHeaders headers = new HttpHeaders();
            String filename = String.format("pedido_%s.xlsx", LocalDate.now());
            headers.add("Content-Disposition", "attachment; filename=" + filename);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(out.toByteArray());
        } catch (IOException e) {
            logger.error("Error de IO al generar el Excel del pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(("Error de IO al generar el Excel: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            logger.error("Error inesperado al generar el Excel del pedido: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(("Error inesperado al procesar la solicitud: " + e.getMessage()).getBytes());
        }
    }
}
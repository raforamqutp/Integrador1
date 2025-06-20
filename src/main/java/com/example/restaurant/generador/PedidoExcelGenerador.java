package com.example.restaurant.generador;

import com.example.restaurant.dto.ItemDTO;
import com.example.restaurant.dto.PedidoDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class PedidoExcelGenerador {

    public static void generarExcel(PedidoDTO pedido, OutputStream out) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pedido");
            int rowIdx = 0;

            // Crear Fila de Encabezado
            Row headerRow = sheet.createRow(rowIdx++);
            String[] headers = {"Categoría", "Item", "Cantidad", "Precio Unitario", "Subtotal"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            double totalGeneral = 0.0;

            // Procesar Platos
            if (pedido.getPlatos() != null && !pedido.getPlatos().isEmpty()) {
                int itemsWritten = writeItemsToSheet(sheet, rowIdx, "Platos", pedido.getPlatos());
                if (itemsWritten > 0) {
                    rowIdx += itemsWritten;
                    for (ItemDTO item : pedido.getPlatos().values()) {
                        if (item.getCantidad() > 0) {
                            totalGeneral += item.getCantidad() * item.getPrecio();
                        }
                    }
                }
            }

            // Procesar Bebidas
            if (pedido.getBebidas() != null && !pedido.getBebidas().isEmpty()) {
                boolean platosHabianEscritoItems = pedido.getPlatos() != null &&
                        pedido.getPlatos().values().stream().anyMatch(i -> i.getCantidad() > 0);
                boolean bebidasTienenItemsParaEscribir = pedido.getBebidas().values().stream().anyMatch(i -> i.getCantidad() > 0);

                if (platosHabianEscritoItems && bebidasTienenItemsParaEscribir && rowIdx > 1) { // rowIdx > 1 para asegurar que no es la primera linea despues del header
                    rowIdx++; // Añadir línea en blanco para separación si hubo platos y hay bebidas
                }
                int itemsWritten = writeItemsToSheet(sheet, rowIdx, "Bebidas", pedido.getBebidas());
                if (itemsWritten > 0) {
                    rowIdx += itemsWritten;
                    for (ItemDTO item : pedido.getBebidas().values()) {
                        if (item.getCantidad() > 0) {
                            totalGeneral += item.getCantidad() * item.getPrecio();
                        }
                    }
                }
            }

            // Fila del Total General
            if (totalGeneral > 0 && rowIdx > 1) { // rowIdx > 1 para asegurar que no es la primera linea despues del header
                rowIdx++; // Añadir línea en blanco antes del total
            }
            Row totalRow = sheet.createRow(rowIdx);
            Cell totalLabelCell = totalRow.createCell(headers.length - 2); // Penúltima celda para "Total General:"
            totalLabelCell.setCellValue("Total General:");
            Cell totalValueCell = totalRow.createCell(headers.length - 1); // Última celda para el valor
            totalValueCell.setCellValue(totalGeneral);

            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);
            // Podrías querer alinear a la derecha el texto "Total General:"
            // totalStyle.setAlignment(HorizontalAlignment.RIGHT);
            totalLabelCell.setCellStyle(totalStyle);
            totalValueCell.setCellStyle(totalStyle);
            // Aquí podrías aplicar un formato de moneda a totalValueCell si es necesario
            // DataFormat format = workbook.createDataFormat();
            // totalStyle.setDataFormat(format.getFormat("S/ #,##0.00"));


            // Auto-ajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }

    private static int writeItemsToSheet(Sheet sheet, int startRowIdx, String categoria, Map<String, ItemDTO> items) {
        int currentRowIdx = startRowIdx;
        int itemsWrittenThisCategory = 0;
        for (Map.Entry<String, ItemDTO> entry : items.entrySet()) {
            ItemDTO item = entry.getValue();
            if (item.getCantidad() > 0) {
                Row row = sheet.createRow(currentRowIdx++);
                row.createCell(0).setCellValue(categoria);
                row.createCell(1).setCellValue(entry.getKey()); // Nombre del Item
                row.createCell(2).setCellValue(item.getCantidad());
                row.createCell(3).setCellValue(item.getPrecio());
                row.createCell(4).setCellValue(item.getCantidad() * item.getPrecio());
                itemsWrittenThisCategory++;
            }
        }
        return itemsWrittenThisCategory; // Devuelve el número de filas de items escritas para esta categoría
    }
}
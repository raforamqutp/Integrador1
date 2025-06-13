package com.example.restaurant;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class ExcelRandomGenerator {

    public static void main(String[] args) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("RandomData");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nombre");
            header.createCell(2).setCellValue("Valor");

            Random random = new Random();
            for (int i = 1; i <= 10; i++) {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(i);
                row.createCell(1).setCellValue("Item_" + i);
                row.createCell(2).setCellValue(random.nextInt(1000));
            }

            try (FileOutputStream fileOut = new FileOutputStream("random_data.xlsx")) {
                workbook.write(fileOut);
            }
            System.out.println("Archivo random_data.xlsx generado correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
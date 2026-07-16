package com.demoqa.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    public static List<String[]> readExcelData(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            createManualTestCaseWorkbook(path);
        }

        List<String[]> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(path.toFile()); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                boolean isBlank = true;
                List<String> values = new ArrayList<>();
                for (Cell cell : row) {
                    String cellValue = getCellValue(cell);
                    values.add(cellValue);
                    if (!cellValue.isBlank()) {
                        isBlank = false;
                    }
                }
                if (!isBlank) {
                    rows.add(values.toArray(new String[0]));
                }
            }
        }
        return rows;
    }

    private static void createManualTestCaseWorkbook(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(path.toFile())) {
            Sheet sheet = workbook.createSheet("Manual Test Cases");
            String[] headers = {"FirstName", "LastName", "Email", "Age", "Country", "Skills", "Subscribe", "Agree"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            Object[][] data = {
                    {"Ravi", "Magar", "ravi@example.com", 34, "India", "Java, Selenium", true, true},
                    {"Asha", "Patil", "asha@example.com", 29, "USA", "Python", false, false},
                    {"Kiran", "Sharma", "kiran@example.com", 31, "UK", "Java, Python, Selenium", true, false},
                    {"Neha", "Joshi", "neha@example.com", 27, "India", "Selenium", true, true}
            };

            for (int rowIndex = 0; rowIndex < data.length; rowIndex++) {
                Row row = sheet.createRow(rowIndex + 1);
                Object[] rowData = data[rowIndex];
                for (int col = 0; col < rowData.length; col++) {
                    Object value = rowData[col];
                    if (value instanceof String stringValue) {
                        row.createCell(col).setCellValue(stringValue);
                    } else if (value instanceof Integer intValue) {
                        row.createCell(col).setCellValue(intValue);
                    } else if (value instanceof Boolean boolValue) {
                        row.createCell(col).setCellValue(boolValue);
                    }
                }
            }

            workbook.write(fos);
        }
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}

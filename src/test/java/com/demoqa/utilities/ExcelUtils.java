package com.demoqa.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {
    // Read all non-empty rows from the automation data sheet of the Excel workbook.
    public static List<String[]> readExcelData(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        // If the Excel file does not exist yet, create a workbook with both manual and automation data.
        if (!Files.exists(path)) {
            createManualTestCaseWorkbook(path);
        }

        List<String[]> rows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(path.toFile()); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("Automation Data");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }

            // Skip the header row and collect the remaining data rows.
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

    // Create a workbook with a detailed manual test-case sheet and a compact automation data sheet.
    private static void createManualTestCaseWorkbook(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        try (Workbook workbook = new XSSFWorkbook(); FileOutputStream fos = new FileOutputStream(path.toFile())) {
            createDetailedTestCaseSheet(workbook);
            createAutomationDataSheet(workbook);
            workbook.write(fos);
        }
    }

    private static void createDetailedTestCaseSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Manual Test Cases");
        String[] headers = {"TC ID", "Title", "Preconditions", "Test Steps", "Test Data", "Expected Result", "Priority", "Automation Status"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        Object[][] data = {
                {"TC-001", "Verify form submission with valid input", "The demo app is accessible.", "1. Open the app.\n2. Enter valid first name, last name, email, and age.\n3. Select a country and skills.\n4. Tick the checkboxes.\n5. Upload a valid image.\n6. Click Submit.", "FirstName=Ravi, LastName=Magar, Email=ravi@example.com, Age=34, Country=India, Skills=Java;Selenium", "The form is submitted successfully and the result shows Submitted.", "High", "Automated"},
                {"TC-002", "Verify dropdown and multi-select behavior", "The demo app is loaded with the form visible.", "1. Open the app.\n2. Select a country from the dropdown.\n3. Choose multiple skills from the multi-select list.\n4. Submit the form.", "Country=USA, Skills=Python;Selenium", "The selected country and all chosen skills are reflected in the submission result.", "High", "Automated"},
                {"TC-003", "Verify checkboxes stay unchecked until submission", "The app is open and the form is displayed.", "1. Open the app.\n2. Select one checkbox only.\n3. Observe the result message without clicking Submit.", "Subscribe=true, Agree=false", "The result text remains Ready until Submit is clicked.", "Medium", "Automated"},
                {"TC-004", "Verify file upload field accepts a valid image", "A sample image file is available in the test resources folder.", "1. Open the app.\n2. Select a valid image file using the upload control.\n3. Observe the page state.", "File=sample-image.png", "The file is attached successfully and no upload error is shown.", "High", "Automated"},
                {"TC-005", "Verify status button changes page output", "The app is open and interactive controls are visible.", "1. Open the app.\n2. Click the Status button.\n3. Observe the result message.", "Button=Status", "The result message updates to show the status action.", "Medium", "Automated"},
                {"TC-006", "Verify form validation for empty mandatory fields", "The app is open.", "1. Leave required fields empty.\n2. Click Submit.\n3. Check the validation behavior.", "All required fields empty", "The user is prompted to complete the required fields before submission.", "High", "Manual"},
                {"TC-007", "Verify multi-select retains multiple skill selections", "The form is loaded with the skills dropdown available.", "1. Open the app.\n2. Select several skills from the multi-select control.\n3. Submit the form.", "Skills=Java;Python;Selenium", "All selected skills remain visible in the form result.", "Medium", "Automated"},
                {"TC-008", "Verify dropdown options are available", "The app is open.", "1. Open the app.\n2. Expand the country dropdown.\n3. Review the listed values.", "Country options=India;USA;UK", "The dropdown contains the expected options for selection.", "Low", "Manual"},
                {"TC-009", "Verify file download flow", "The download feature is enabled on the page.", "1. Open the app.\n2. Click the download control if present.\n3. Verify the file is downloaded.", "File type=image/png", "The file is downloaded successfully to the expected location.", "Medium", "Manual"},
                {"TC-010", "Verify submit button visibility and handling", "The form is loaded in the browser.", "1. Open the app.\n2. Locate the Submit button.\n3. Confirm it is visible and clickable.", "Button=Submit", "The Submit button is visible and responds to user action.", "High", "Automated"}
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
    }

    private static void createAutomationDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Automation Data");
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
    }

    // Convert an Excel cell into a readable string value.
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

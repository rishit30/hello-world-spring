package com.resource_inventory.resource_inventory.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

@SpringBootApplication
@RestController
@RequestMapping("/api/excel")
public class ExcelToJsonController {

    @GetMapping("/to-json")
    public String convertExcelToJson() {
        String filePath = "/Users/rishitkumar/Downloads/resource-inventory/src/main/resources/AzureResourceInventory_Report_2025-01-14_14_00.xlsx";
        try (InputStream inputStream = new FileInputStream(new File(filePath)); Workbook workbook = new XSSFWorkbook(inputStream)) {
            JSONArray jsonOutput = new JSONArray();

            for (Sheet sheet : workbook) {
                JSONObject sheetJson = new JSONObject();
                sheetJson.put("tab", sheet.getSheetName());
                JSONArray detailsArray = new JSONArray();

                if (sheet.getSheetName().equalsIgnoreCase("Overview")) {
                    // Handle the "Overview" sheet specifically
                    for (int rowIndex = 4; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        if (row != null) {
                            Cell nameCell = row.getCell(0);
                            Cell countCell = row.getCell(1);

                            if (nameCell != null && countCell != null) {
                                String name = nameCell.toString().trim();
                                String count = countCell.toString().trim();

                                if (!name.isEmpty() && !count.isEmpty()) {
                                    JSONObject rowJson = new JSONObject();
                                    rowJson.put("Name", name);
                                    rowJson.put("Count", count);
                                    detailsArray.put(rowJson);
                                }
                            }
                        }
                    }
                } else {
                    // Handle other sheets
                    Iterator<Row> rowIterator = sheet.iterator();
                    boolean headerIdentified = false;
                    List<String> headers = new ArrayList<>();

                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        if (!headerIdentified) {
                            // Detect header row
                            for (Cell cell : row) {
                                String cellValue = cell.getStringCellValue().trim();
                                if (!cellValue.isEmpty()) {
                                    headers.add(cellValue);
                                }
                            }
                            if (!headers.isEmpty()) {
                                headerIdentified = true;
                            }
                        } else {
                            // Process data rows
                            JSONObject rowJson = new JSONObject();
                            for (int i = 0; i < headers.size(); i++) {
                                Cell cell = row.getCell(i);
                                String cellValue = cell != null ? cell.toString() : "";
                                rowJson.put(headers.get(i), cellValue);
                            }
                            detailsArray.put(rowJson);
                        }
                    }
                }

                sheetJson.put("details", detailsArray);
                jsonOutput.put(sheetJson);
            }

            return jsonOutput.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing file: " + e.getMessage();
        }
    }
}





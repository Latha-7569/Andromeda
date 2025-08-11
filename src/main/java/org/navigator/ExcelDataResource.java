package org.navigator;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.InputStream;
import java.util.*;

@Path("/excel")
public class ExcelDataResource {
	@GET
	@Path("/dropdowns")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDropdownData() {
	    try (InputStream excelStream = ExcelDataResource.class.getResourceAsStream("/RegistrationDetails.xlsx")) {
	        if (excelStream == null) {
	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                    .entity("{\"error\":\"Excel file not found\"}").build();
	        }

	        try (Workbook workbook = new XSSFWorkbook(excelStream)) {
	            Sheet supertypeSheet = workbook.getSheet("SuperType");
	            List<String> superTypes = new ArrayList<>();
	            for (Row row : supertypeSheet) {
	                if (row.getRowNum() == 0) continue;
	                Cell cell = row.getCell(0);
	                if (cell != null && cell.getCellType() == CellType.STRING) {
	                    superTypes.add(cell.getStringCellValue().trim());
	                }
	            }

	            Sheet typeSheet = workbook.getSheet("Type");
	            Map<String, List<String>> typesMap = new HashMap<>();
	            String firstSuperType = superTypes.isEmpty() ? "defaultSuperType" : superTypes.get(0);
	            List<String> types = new ArrayList<>();
	            for (Row row : typeSheet) {
	                if (row.getRowNum() == 0) continue;
	                Cell cell = row.getCell(0);
	                if (cell != null && cell.getCellType() == CellType.STRING) {
	                    types.add(cell.getStringCellValue().trim());
	                }
	            }
	            typesMap.put(firstSuperType, types);
	            Sheet subTypeSheet = workbook.getSheet("SubTypes");
	            Map<String, List<String>> subTypeMap = new HashMap<>();
	            Map<String, List<String>> apnMap = new HashMap<>();
	            for (Row row : subTypeSheet) {
	                if (row.getRowNum() == 0) continue;
	                Cell partNameCell = row.getCell(0);
	                Cell subPartCell = row.getCell(1);
	                Cell apnCell = row.getCell(2);

	                if (partNameCell == null || subPartCell == null || apnCell == null) continue;

	                String partName = partNameCell.getStringCellValue().trim();
	                String[] subParts = subPartCell.getStringCellValue().trim().split(",");
	                String[] apns = apnCell.getStringCellValue().trim().split(",");

	                if (subParts.length != apns.length) continue;

	                List<String> subPartList = new ArrayList<>();
	                List<String> apnList = new ArrayList<>();
	                for (int i = 0; i < subParts.length; i++) {
	                    String subPart = subParts[i].trim();
	                    String apn = apns[i].trim();
	                    subPartList.add(subPart);
	                    apnList.add(apn + "-" + subPart);
	                }
	                subTypeMap.put(normalizeKey(partName), subPartList);
	                apnMap.put(normalizeKey(partName), apnList);
	            }
	            Map<String, Object> responseMap = new HashMap<>();
	            responseMap.put("superTypes", superTypes);
	            responseMap.put("types", typesMap);
	            responseMap.put("subtypes", subTypeMap);
	            responseMap.put("apn", apnMap);


	            return Response.ok(responseMap).build();

	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity("{\"error\":\"Failed to read Excel\"}").build();
	    }
	}
	private String normalizeKey(String key) {
	    if (key == null) return null;
	    return key.replaceAll("\\s+", "").toLowerCase();
	}
}

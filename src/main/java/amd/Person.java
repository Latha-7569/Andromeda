package amd;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Person {
	//Testing commit
    public static final String sFilePath = "C:\\Users\\Public\\NikhilWorkspace\\WebserviceLearning\\AndromedaData.xlsx";
    public static final String sFileSheetName = "Authentication";
    public String objectId;
	public static final String OBJECT_ID="ObjectId";

    public Person(String ObjectId) {
        this.objectId = ObjectId;
    }

    public Person() {
    	
    }

    public Map<String, String> getPersonInfos() {
        String s = this.objectId; 
        Map<String, String> returnMap = new HashMap<>();
        FileInputStream fis = null;
        Workbook wb = null;

        try {
            fis = new FileInputStream(sFilePath);
            wb = new XSSFWorkbook(fis); 
            Sheet sheet = wb.getSheet(sFileSheetName);
            
            if (sheet == null) {
                System.out.println("Sheet not found");
                return null;
            }
            
            DataFormatter formatter = new DataFormatter();
            int rowCount = sheet.getLastRowNum();
            
            for (int i = 1; i <= rowCount; i++) { 
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(0);
                if (cell == null) continue;
                
                String cellValue = formatter.formatCellValue(cell).trim();
                if (cellValue.equals(s)) {
                    Row headerRow = sheet.getRow(0);  
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell headerCell = headerRow.getCell(j); 
                        Cell dataCell = row.getCell(j);         
                        if (headerCell != null && dataCell != null) {
                            String key = formatter.formatCellValue(headerCell).trim();
                            String value = formatter.formatCellValue(dataCell).trim();
                            returnMap.put(key, value);  
                        }
                    }
                    return returnMap; 
                }
            }

            throw new ObjectIdNotFoundException("ObjectId '" + s + "' not found in DataBase: " + sFileSheetName);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public class ObjectIdNotFoundException extends RuntimeException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ObjectIdNotFoundException(String message) {
			super(message);
		}
	}
  //getPersons
    public static List<Map<String, String>> getPersonsFromDB() {
        List<Map<String, String>> personsList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(sFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sFileSheetName);  
            int totalRows = sheet.getLastRowNum();
            Row headerRow = sheet.getRow(0); 
            int totalCols = headerRow.getLastCellNum();
            
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                boolean isRowEmpty = true;
                for (int j = 0; j < totalCols; j++) {
                    Cell cell = row.getCell(j);
                    if (cell != null && cell.getCellType() != CellType.BLANK) {
                        isRowEmpty = false;
                        break;
                    }
                }
                if (isRowEmpty) continue;
                
                Map<String, String> personMap = new LinkedHashMap<>();
                DataFormatter formatter = new DataFormatter();
                for (int j = 0; j < totalCols; j++) {
                    Cell headerCell = headerRow.getCell(j);
                    Cell dataCell = row.getCell(j);
                    String key = headerCell != null ? headerCell.toString().trim().toLowerCase() : "column" + j;
                    String value = dataCell != null ? formatter.formatCellValue(dataCell) : "";
                    personMap.put(key, value);
                }
                personsList.add(personMap);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return personsList;
    }
    
    //setAccess
    public void setAccess(String sAccess) {

        if (objectId != null && !objectId.isEmpty()) {
            Person p = new Person(objectId);
            AmxSchemasrules amx = new AmxSchemasrules();
            Map<String, Object> data = amx.PersonAccess();

            List<String> ll = (List<String>) data.get("Person Access");

            if (ll != null && ll.contains(sAccess)) {
                FileInputStream fis = null;
                Workbook wb = null;
                boolean found = false;
                try {
                    fis = new FileInputStream(sFilePath);
                    wb = new XSSFWorkbook(fis);
                    Sheet sh = wb.getSheet(sFileSheetName);
                    DataFormatter df = new DataFormatter();

                    for (int i = 0; i <= sh.getLastRowNum(); i++) {
                        Row row = sh.getRow(i);
                        if (row == null) continue;

                        Cell cdata = row.getCell(7);
                        String value = df.formatCellValue(cdata);

                        if (objectId.equals(value)) {
                            Cell cell = row.getCell(8);
                            if (cell == null) {
                                cell = row.createCell(8);
                            }
                            cell.setCellValue(sAccess);
                            try (FileOutputStream fos = new FileOutputStream(sFilePath)) {
                                wb.write(fos);
                            }

                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        throw new ObjectIdNotFoundException(objectId + " is not found in Database");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("An error occurred while setting access", e);
                } finally {
                    try {
                        if (fis != null) fis.close();
                        if (wb != null) wb.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                throw new ObjectIdNotFoundException(objectId + " is not found in Database. Check with admin.");
            }
        } else {
            throw new IllegalArgumentException("ObjectId is empty");
        }
    }

    
    //getAccess
    
    public String getAccess() {
        String sFinalValue = "";
        FileInputStream fis = null;
        Workbook wb = null;
        boolean found = false;
        try {
            fis = new FileInputStream(sFilePath);
            wb = new XSSFWorkbook(fis);
            Sheet sh = wb.getSheet(sFileSheetName);
            DataFormatter df = new DataFormatter();

            for (int i = 0; i <= sh.getLastRowNum(); i++) {
                Row row = sh.getRow(i);
                if (row == null) continue;

                Cell cell = row.getCell(7); 
                if (cell == null) continue;

                String sobjid = df.formatCellValue(cell);
                if (sobjid.equals(objectId)) {
                    found = true;
                    Cell value = row.getCell(8); 
                    if (value != null) {
                        sFinalValue = df.formatCellValue(value);
                    }
                    break;
                }
            }

            if (!found) {
                throw new ObjectIdNotFoundException(objectId + " is not found in Database");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("An error occurred while getting access", e);
        } finally {
            try {
                if (fis != null) fis.close();
                if (wb != null) wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sFinalValue;
    }
    
    // update person
    public void updatePersonInExcel(String filePath, String objectId, Map<String, String> updatedFields) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);

        int objectIdColIndex = findColumnIndex(sheet, "ObjectId");
        if (objectIdColIndex == -1) {
            workbook.close();
            fis.close();
            throw new RuntimeException("ObjectId column not found in sheet.");
        }

        String searchId = objectId.trim().toLowerCase();
        boolean updated = false;
        for (Row row : sheet) {
            Cell idCell = row.getCell(objectIdColIndex);
            if (idCell != null) {
                idCell.setCellType(CellType.STRING);
                String cellValue = idCell.getStringCellValue().trim();
                //System.out.println("'" + cellValue + "'"); 

                if (searchId.equals(cellValue.toLowerCase())) {
                 
                    for (Map.Entry<String, String> entry : updatedFields.entrySet()) {
                        int colIndex = findColumnIndex(sheet, entry.getKey());
                        if (colIndex != -1) {
                            Cell cell = row.getCell(colIndex);
                            if (cell == null) cell = row.createCell(colIndex);
                            cell.setCellValue(entry.getValue());
                        }
                    }
                    updated = true;
                    break; 
                }
            }
        }

        fis.close();

        if (!updated) {
            workbook.close();
            throw new RuntimeException("ObjectId '" + objectId + "' not found in sheet.");
        }


        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        fos.close();
        workbook.close();
    }
    public int findColumnIndex(Sheet sheet, String columnName) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) return -1;

        for (Cell cell : headerRow) {
            cell.setCellType(CellType.STRING);
            if (columnName.equalsIgnoreCase(cell.getStringCellValue().trim())) {
                return cell.getColumnIndex();
            }
        }
        return -1;
    }

    
    
}

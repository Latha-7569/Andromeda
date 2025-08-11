package amd;

import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;


/*Custom Class For API
 * In getInfo() need to enter the sObjectId 
 * based on sobjectId we retrieve the data
 * Enters with the what type of field needed that field returns with the getInfo method.
 * 
 * */
public class DataFetchAMD {
	public static final String sFilePath = "C:\\Users\\Public\\NikhilWorkspace\\WebserviceLearning\\RegistrationDetails.xlsx";
	public static final String sFileSheetName = "MainDataBase";
	public String objectId;
	public JSONObject loggedInUsername;

	public static final String OBJECT_ID="ObjectId";
	public static final String APN="APN";
	public static final String NAME="Name";
	public static final String SUPER_TYPE="SuperType";
	public static final String TYPE="Type";
	public static final String DESCRIPTION="Description";
	public static final String CREATED_DATE="CreatedDate";
	public static final String OWNER="Owner";
	public static final String EMAIL_ID="EmailId";

	/** Constructor with passing  argument */
	public DataFetchAMD(String ObjectId) {
		this.objectId=ObjectId;
	}
	
	
	/** Constructor without passing any argument*/
	public DataFetchAMD() {
	}
	/**
	 * Fetches data from an Excel sheet based on a given object ID.
	 * Retrieves data from the Excel file matching the object ID.
	 *
	 * @return Map<String, String> if found; null otherwise
	 */
	
	
	public Map getInfos() {
		Map returnMap = new HashMap();
		FileInputStream fis = null;
		Workbook wb= null;
		try {
			fis = new FileInputStream(sFilePath);
			wb= new XSSFWorkbook(fis);
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
				if (cellValue.equals(objectId)) {
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
			throw new ObjectIdNotFoundException("ObjectId '" + objectId + "' not found in DataBase: " + sFileSheetName);
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
			return null;
		} 
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * */
	public String getInfo(String sSelectables) throws IOException {
		String sFinalOutput = null;
		boolean objectIdFound=false;

		if (sSelectables != null && !sSelectables.isEmpty()) {
			FileInputStream fis = new FileInputStream(sFilePath);
			Workbook workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheet(sFileSheetName);
			Row header = sheet.getRow(0);
			int objectIdCol = -1;
			int targetCol = -1;
			for (int i = 0; i < header.getLastCellNum(); i++) {
				Cell cell = header.getCell(i);
				if (cell != null) {
					String headerName = cell.toString().trim();
					if (headerName.equalsIgnoreCase(OBJECT_ID)) {
						objectIdCol = i;
					} else if (headerName.equalsIgnoreCase(sSelectables)) {
						targetCol = i;
					}
				}
			}
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) continue;
				Cell idCell = row.getCell(objectIdCol);
				if (idCell == null) continue;
				String idValue = idCell.toString().trim();
				if (idValue.equalsIgnoreCase(this.objectId)) {
					Cell targetCell = row.getCell(targetCol);
					if (targetCell != null) {
						sFinalOutput = targetCell.toString().trim();
					}
					break;
				}
			}
			workbook.close();
			fis.close();

			if (!objectIdFound) {
				throw new ObjectIdNotFoundException("ObjectId '" + objectId + "' not found in DataBase: " + sFileSheetName);
			}

		}
		return sFinalOutput;
	}   
	
	/**
	 * Adds a new row to the Excel sheet using data from a JSON string.
	 *
	 * @param sObjectDatas 
	 */
	public void createObject(String sObjectDatas) {
		if (!sObjectDatas.isEmpty()) {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			Workbook wb = null;
			try {
				fis = new FileInputStream(sFilePath);
				wb = new XSSFWorkbook(fis);
				Sheet sh = wb.getSheet(sFileSheetName);
				if (sh == null) {
					throw new ObjectIdNotFoundException("ObjectId '" + objectId + "' not found in DataBase: " + sFileSheetName);
				}
				JSONObject json = new JSONObject(sObjectDatas);
				Map mpjsondata = json.toMap();

				Iterator iteratorKeys = json.keys();
				while (iteratorKeys.hasNext()) {
					String skey = (String) iteratorKeys.next();
					String svalue = json.getString(skey);
					mpjsondata.put(skey, svalue);
				}
				DataFormatter formatter = new DataFormatter();
				int rown = sh.getLastRowNum() + 1;
				Row rn = sh.createRow(rown);
				Row headerrow = sh.getRow(0);
				if (headerrow == null) {
					throw new RuntimeException("Header row is missing in the sheet");
				}

				for (int i = 0; i < headerrow.getLastCellNum(); i++) {
					Cell cell = headerrow.getCell(i);
					String header = formatter.formatCellValue(cell).trim();
					String value = (String) mpjsondata.getOrDefault(header, "");
					rn.createCell(i).setCellValue(value);
				}
				fos = new FileOutputStream(sFilePath);
				wb.write(fos);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} 
		}
	}

	/**
	 * Returns a list of maps containing the latest 10 rows 
	 * In Excel data with column Keys and values
	 * @return List
	 * @throw Exception 
	 */
	public static   List getLatestPartsFromDB() {
		List finallist = new ArrayList();
		try (FileInputStream fis = new FileInputStream(sFilePath);
				Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheet(sFileSheetName);
			int totalRows = sheet.getLastRowNum(); 
			Row headerRow = sheet.getRow(0);
			int totalCols = headerRow.getLastCellNum(); 
			int nonEmptyCount = 0;
			for (int i = totalRows; i >= 1 && nonEmptyCount < 10; i--) {
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
				Map rowMap = new LinkedHashMap();
				for (int j = 0; j < totalCols; j++) {
					Cell headerCell = headerRow.getCell(j);
					Cell dataCell = row.getCell(j);
					String key = headerCell != null ? headerCell.toString() : "Column" + j;
					String value = "";
					if (dataCell != null) {
						DataFormatter formatter = new DataFormatter();
						value = formatter.formatCellValue(dataCell);
					}
					rowMap.put(key, value);
				}
				finallist.add(rowMap);
				nonEmptyCount++;
			}

		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return finallist;
	}
	
	
	public void deleteObject() {
		if (objectId.isEmpty()) {
			throw new ObjectIdNotFoundException("ObjectId '" + objectId + "' not found in DataBase: " + sFileSheetName);
		} else {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			Workbook wb = null;

			try {
				fis = new FileInputStream(sFilePath);
				wb = new XSSFWorkbook(fis);
				Sheet sh = wb.getSheet(sFileSheetName);

				if (sh == null) {
					throw new ObjectIdNotFoundException("Sheet not found: " + sFileSheetName);
				}

				DataFormatter formatter = new DataFormatter();
				boolean deleted = false;
				int rowcount = sh.getLastRowNum();

				for (int i = 1; i <= rowcount; i++) {
					Row row = sh.getRow(i);
					if (row == null) continue;

					Cell cell = row.getCell(3);
					if (cell == null) continue;

					String value = formatter.formatCellValue(cell).trim();
					System.out.println(value);
					if (value.equalsIgnoreCase(objectId.trim())) {
						int lastRow = sh.getLastRowNum();
						if (i < lastRow) {
							sh.shiftRows(i + 1, lastRow, -1);
						} else {
							sh.removeRow(row);
						}
						deleted = true;
						break;
					}
				}

				if (!deleted) {
					throw new ObjectIdNotFoundException("ObjectId '" + objectId + "' not found in the sheet.");
				}

				fis.close();
				fos = new FileOutputStream(sFilePath);
				wb.write(fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
}


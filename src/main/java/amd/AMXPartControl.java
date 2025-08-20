package amd;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.json.JSONObject;
public class AMXPartControl {
	public static final String sfilepath="C:\\Users\\Public\\NikhilWorkspace\\WebserviceLearning\\AndromedaData.xlsx";
	public static final String ssheetName="AMXPartControl";
	public String Objectid;
	public final static String  OBJECT_ID="objectid";
	public final static String  Type="Type";
	public final static String  Name="Name";
	public final static String  Description="Description";
	public final static String  created_Date="CreatedDate";
	public final static String  Owner="Owner";
	public final static String  Email="Email";
	
	
	/*
	 * Construct with passing Argument.
	 */
	public AMXPartControl(String Objectid ){
		this. Objectid= Objectid;
	}
	/*
	 * Construct without passing Argument.
	 */
	public AMXPartControl(){
	}
	
 /**
  * Fetches data from an Excel sheet based on a given object ID.
  * Retrieves data from the Excel file matching the object ID.
  * @throws Exception 
  * @return Map
 */
public Map getInfos() {
	 Map returnMap=new LinkedHashMap();
	 boolean ispresent=false;
	 try {
		 FileInputStream fis=new FileInputStream (sfilepath);
		 XSSFWorkbook wb=new XSSFWorkbook(fis);
		 XSSFSheet sh=wb.getSheet(ssheetName);
		 DataFormatter formatter=new DataFormatter();
		 
		 for(int i=0;i<sh.getLastRowNum();i++) {
		Row row=sh.getRow(i);
		Cell cell=row.getCell(0);
		
		String str=formatter.formatCellValue(cell).trim();
		
		
		if(str.equals(Objectid)) {
			ispresent=true;
		
			XSSFRow rowheader=sh.getRow(0);
		
			for(int j=0;j<rowheader.getLastCellNum();j++) {
			
				XSSFCell keys=rowheader.getCell(j);
				System.out.println(keys);
				Cell datas=row.getCell(j);
				if(keys!=null&&datas!=null) {
				String value=formatter.formatCellValue(keys).trim();

				String valkey=formatter.formatCellValue(datas).trim();
				returnMap.put(value, valkey);
			
			}
				System.out.println(returnMap);
			}
	}else {
		continue;
	
		}
		 }
		 if(!ispresent) {
			 throw new ObjecNotFoundException("object found is not in te database");
		 }
		 
		
	 }catch(Exception e) {
		 e.printStackTrace();
		
	 }
	return returnMap;

 }

 /**
  * Read data from the Excel file and match the Object ID using column indexes to retrieve the required information.  
  *  @throws Exception 
 * @param selectable
 * @return String 
 */
public String getInfo(String selectable) {
	 String s="";
	 boolean ispresent=false;
	 try {
		 FileInputStream fis=new FileInputStream(sfilepath);
		 XSSFWorkbook wb=new  XSSFWorkbook(fis);
		 XSSFSheet sh=wb.getSheet("AmxPartControl");
		 DataFormatter formatter=new  DataFormatter ();
		 int objectidcol=-1;
		 int targetcol=-1;
		 Row headers=sh.getRow(0);
		 for(int i=0;i<headers.getLastCellNum();i++) {

			 Cell cell=headers.getCell(i);
			 if(cell!=null) {

				
			 String sheaders =formatter.formatCellValue(cell).trim();
			
			

			 if(sheaders.equalsIgnoreCase(OBJECT_ID)) {
				 
				
				 objectidcol=i;				
			 }else if(sheaders.equalsIgnoreCase(selectable)) {
				 targetcol=i;
				 
			 }
		 }
		 }
		
		 for(int i=0;i<sh.getLastRowNum();i++) {
			 Row row=sh.getRow(i);
			
			 Cell cell=row.getCell(objectidcol);
			
			 String sobjectid=formatter.formatCellValue(cell).trim();
			 if(sobjectid.equalsIgnoreCase(this.Objectid.trim())){
				 ispresent=true;
				Cell data=row.getCell(targetcol);
				if(data!=null) {
				s=formatter.formatCellValue(data).trim(); 
				break;
			 }
			
		 }else {
			 continue;
		 }
		 }
		 
		 if(!ispresent) {
			 throw new ObjecNotFoundException("object id is not found in database"); 
		 }
		
		 
	 }catch(Exception e) {
		 e.getMessage();
		 e.printStackTrace();
	 }
	return s;
	 
 }

 /**
  *Add a new row in excel Sheet data using  in the form JSON string 
  * @throws Exception 
 * @param Sojectdatas
 */
public void createPartControlObject(String sojectdatas) {
    FileInputStream fis = null;
    FileOutputStream fos = null;
    XSSFWorkbook wb = null;

    try {
        File file = new File(sfilepath);

        if (!file.exists()) {
            wb = new XSSFWorkbook();
            XSSFSheet sh = wb.createSheet(ssheetName);
            Row headerRow = sh.createRow(0);

            String[] headers = {"ObjectId", "Name", "SuperType", "Type", "Description", "CreatedDate", "Owner", "EmailId", "Assignee", "ConnectionId"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            fos = new FileOutputStream(sfilepath);
            wb.write(fos);
            fos.close();
            wb.close();
        }

        fis = new FileInputStream(sfilepath);
        wb = new XSSFWorkbook(fis);
        XSSFSheet sh = wb.getSheet(ssheetName);
        if (sh == null) {
            sh = wb.createSheet(ssheetName);
            Row headerRow = sh.createRow(0);
            String[] headers = {"ObjectId", "Name", "SuperType", "Type", "Description", "CreatedDate", "Owner", "EmailId", "Assignee", "ConnectionId"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
        }

        DataFormatter formatter = new DataFormatter();
        JSONObject json = new JSONObject(sojectdatas);

        // Read JSON keys and store lowercase keys and values
        Map<String, String> datamap = new LinkedHashMap<>();
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = json.getString(key);
            datamap.put(key.toLowerCase().trim(), value);
        }

        // Explicit header -> JSON key mapping
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("objectid", "objectid");
        headerMap.put("name", "name");
        headerMap.put("supertype", "supertype");
        headerMap.put("type", "type");
        headerMap.put("description", "description");
        headerMap.put("createddate", "createddate");
        headerMap.put("owner", "owner");
        headerMap.put("emailid", "email");   // <-- map Excel "EmailId" to JSON key "email"
        headerMap.put("assignee", "assignee");
        headerMap.put("connectionid", "connectionid");

        int newRowIndex = -1;
        int lastRowNum = sh.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sh.getRow(i);
            if (row == null || row.getCell(0) == null || row.getCell(0).toString().trim().isEmpty()) {
                newRowIndex = i;
                break;
            }
        }
        if (newRowIndex == -1) {
            newRowIndex = lastRowNum + 1;
        }

        Row headerRow = sh.getRow(0);
        Row newRow = sh.createRow(newRowIndex);

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell headerCell = headerRow.getCell(i);
            if (headerCell == null) continue;

            String header = formatter.formatCellValue(headerCell).trim().toLowerCase();
            String jsonKey = headerMap.get(header);
            String value = "";
            if (jsonKey != null) {
                value = datamap.getOrDefault(jsonKey.toLowerCase(), "");
            }
            newRow.createCell(i).setCellValue(value);
        }

        fis.close();
        fis = null;

        fos = new FileOutputStream(sfilepath);
        wb.write(fos);

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
            if (wb != null) wb.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}



 public String  getType()throws Exception {
	return getInfo(Type);
 }
 public String  getName()throws Exception {
	return getInfo(Name);
 }
 public String  getDescription()throws Exception {
	return getInfo(Description);
 }
 public String  getCreateddate()throws Exception {
	return getInfo(created_Date);
 }
 public String  getOwner()throws Exception {
	return getInfo(Owner);
 }
 public String  getEmail()throws Exception {
	return getInfo(Email);
 }
 public class  ObjecNotFoundException extends RuntimeException {
	 public ObjecNotFoundException(String message) {
		 super(message);
	 }
 }
 public String generateNextPartName() throws Exception {
	    FileInputStream fis = null;
	    try {
	        fis = new FileInputStream(sfilepath);
	        XSSFWorkbook wb = new XSSFWorkbook(fis);
	        XSSFSheet sh = wb.getSheet(ssheetName);
	        DataFormatter formatter = new DataFormatter();

	        int nameColIndex = -1;
	        Row headerRow = sh.getRow(0);
	        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
	            Cell cell = headerRow.getCell(i);
	            if (cell != null) {
	                String header = formatter.formatCellValue(cell).trim();
	                if ("Name".equalsIgnoreCase(header)) {
	                    nameColIndex = i;
	                    break;
	                }
	            }
	        }
	        if (nameColIndex == -1) {
	            throw new Exception("Name column not found in Excel sheet");
	        }

	        int maxNumber = 0;
	        int lastRowNum = sh.getLastRowNum();

	        for (int i = 1; i <= lastRowNum; i++) { 
	            Row row = sh.getRow(i);
	            if (row != null) {
	                Cell cell = row.getCell(nameColIndex);
	                if (cell != null) {
	                    String name = formatter.formatCellValue(cell).trim();
	                    if (name.startsWith("PC-")) {
	                        String numStr = name.substring(3);
	                        try {
	                            int num = Integer.parseInt(numStr);
	                            if (num > maxNumber) {
	                                maxNumber = num;
	                            }
	                        } catch (NumberFormatException nfe) {
	                        }
	                    }
	                }
	            }
	        }
	        int nextNum = maxNumber + 1;
	        return String.format("PC-%06d", nextNum);

	    } finally {
	        if (fis != null) {
	            fis.close();
	        }
	    }
 	}
}

 

 
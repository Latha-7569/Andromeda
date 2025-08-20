package amd;
import java.io.FileInputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
public class AmxSchemasrules {
	 public  static final String sFilepath="C:\\Users\\Public\\NikhilWorkspace\\WebserviceLearning\\AndromedaData.xlsx";
	 public static final String ssheetname="AMXSchemaRules";
	public Map PersonAccess() {
	
	Map finalmap=new HashMap();
	List list=new LinkedList();
	FileInputStream fis=null;
	Workbook wb=null;
	try {
		fis =new FileInputStream(sFilepath);
		wb=new XSSFWorkbook(fis);
		Sheet sh=wb.getSheet( ssheetname);
		DataFormatter df=new DataFormatter();
		for(int i=0;i<sh.getLastRowNum();i++) {
			Row row=sh.getRow(i);
			Cell cell=row.getCell(0);
			
			String key =df.formatCellValue(cell) ;
		if("PersonAccess".equalsIgnoreCase(key)) {
			Cell value=row.getCell(1);
			String svalue=df.formatCellValue(value);
		
			String data=svalue;
			//System.out.println(data);
			
			String [] parts=data.split("\\|");
			for(int i1=0;i1<parts.length;i1++) {	
			list.add(parts[i1].trim());
			}
			finalmap.put("Person Access",list);
		}
		}
	}catch(Exception e) {
		e.getMessage();
		e.printStackTrace();
	}
	return finalmap;
	
}
	
	public Map partAccess() {
		
		Map finalmap=new HashMap();
		List list=new LinkedList();
		FileInputStream fis=null;
		Workbook wb=null;
		try {
			fis =new FileInputStream(sFilepath);
			wb=new XSSFWorkbook(fis);
			Sheet sh=wb.getSheet( ssheetname);
			DataFormatter df=new DataFormatter();
			for(int i=0;i<sh.getLastRowNum();i++) {
				Row row=sh.getRow(i);
				Cell cell=row.getCell(0);
				
				String key =df.formatCellValue(cell) ;
			if("PartAccess".equalsIgnoreCase(key)) {
				Cell value=row.getCell(1);
				String svalue=df.formatCellValue(value);
			
				String data=svalue;
				//System.out.println(data);
				
				String [] parts=data.split("\\|");
				for(int i1=0;i1<parts.length;i1++) {	
			
				list.add(parts[i1].trim());
					
				}
				finalmap.put("partAccess",list);
			}
			}
		}catch(Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
		return finalmap;
		
	}
public Map partControl() {
		
		Map finalmap=new HashMap();
		List list=new LinkedList();
		FileInputStream fis=null;
		Workbook wb=null;
		try {
			fis =new FileInputStream(sFilepath);
			wb=new XSSFWorkbook(fis);
			Sheet sh=wb.getSheet(ssheetname);
			DataFormatter df=new DataFormatter();
			for(int i=0;i<sh.getLastRowNum();i++) {
				Row row=sh.getRow(i);
				Cell cell=row.getCell(0);
				
				String key =df.formatCellValue(cell) ;
			if("PartControl".equalsIgnoreCase(key)) {
				Cell value=row.getCell(1);
				String svalue=df.formatCellValue(value);
			
				String data=svalue;
				//System.out.println(data);
				
				String [] parts=data.split("\\|");
				for(int i1=0;i1<parts.length;i1++) {	
			
				list.add(parts[i1].trim());

					
				}
				finalmap.put("partControl",list);
			}
			}
		}catch(Exception e) {
			e.getMessage();
			e.printStackTrace();
		}
		return finalmap;
		
	}
public Map partStates() {
	
	Map finalmap=new HashMap();
	List list=new LinkedList();
	FileInputStream fis=null;
	Workbook wb=null;
	try {
		fis =new FileInputStream(sFilepath);
		wb=new XSSFWorkbook(fis);
		Sheet sh=wb.getSheet( ssheetname);
		DataFormatter df=new DataFormatter();
		for(int i=0;i<sh.getLastRowNum();i++) {
			Row row=sh.getRow(i);
			Cell cell=row.getCell(0);
			
			String key =df.formatCellValue(cell) ;
		if("PartStates".equalsIgnoreCase(key)) {
			Cell value=row.getCell(1);
			String svalue=df.formatCellValue(value);
		
			String data=svalue;
			
			
			String [] parts=data.split("\\|");
			for(int i1=0;i1<parts.length;i1++) {	
		
			list.add(parts[i1].trim());

				
			}
			finalmap.put("PartStates",list);
		}
		}
	}catch(Exception e) {
		e.getMessage();
		e.printStackTrace();
	}
	return finalmap;
	
}
public Map partControlStates() {
	
	Map finalmap=new HashMap();
	List list=new LinkedList();
	FileInputStream fis=null;
	Workbook wb=null;
	try {
		fis =new FileInputStream(sFilepath);
		wb=new XSSFWorkbook(fis);
		Sheet sh=wb.getSheet( ssheetname);
		DataFormatter df=new DataFormatter();
		for(int i=0;i<sh.getLastRowNum();i++) {
			Row row=sh.getRow(i);
			Cell cell=row.getCell(0);
			
			String key =df.formatCellValue(cell) ;
		if("PartControlStates".equalsIgnoreCase(key)) {
			Cell value=row.getCell(1);
			String svalue=df.formatCellValue(value);
		
			String data=svalue;
			//System.out.println(data);
			
			String [] parts=data.split("\\|");
			for(int i1=0;i1<parts.length;i1++) {	
		
			list.add(parts[i1].trim());

				
			}
			finalmap.put("PartControlStates",list);
		}
		}
	}catch(Exception e) {
		e.getMessage();
		e.printStackTrace();
	}
	return finalmap;
	
}

}

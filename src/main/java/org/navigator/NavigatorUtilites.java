package org.navigator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.json.JSONArray;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/navigatorutilites")
public class NavigatorUtilites {

    private static final String FILE_PATH = "C:\\Users\\Public\\NikhilWorkspace\\WebserviceLearning\\RegistrationDetails.xlsx";

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@FormParam("username") String username, @Context HttpServletRequest request) {
        JSONObject response = new JSONObject();
        if (username == null || username.isEmpty()) {
            response.put("Status", "Failed");
            response.put("Message", "Username is required.");
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("username", username);
        response.put("Status", "Success");
        response.put("Message", "User logged in.");
        response.put("Username", username);

        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createPart(@FormParam("SuperType") String supertype,@FormParam("Type") String type,@FormParam("APN") String apnWithSubtype,
                               @FormParam("Description") String description,@FormParam("ResponsibleEngineer") String responsibleEngineerParam,@Context HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            String loggedInUsername = (session != null) ? (String) session.getAttribute("username") : null;
            if (loggedInUsername == null) {
                JSONObject error = new JSONObject();
                error.put("Status", "Failed");
                error.put("Message", "User not logged in.");
                return Response.status(Response.Status.UNAUTHORIZED).entity(error.toString()).build();
            }
            if (supertype == null || supertype.isEmpty()
                    || type == null || type.isEmpty()
                    || apnWithSubtype == null || apnWithSubtype.isEmpty()
                    || description == null || description.isEmpty()) {
                JSONObject error = new JSONObject();
                error.put("Status", "Failed");
                error.put("Message", "Missing required fields.");
                return Response.status(Response.Status.BAD_REQUEST).entity(error.toString()).build();
            }
            String responsibleEngineer = responsibleEngineerParam != null && !responsibleEngineerParam.isEmpty()
                    ? responsibleEngineerParam
                    : loggedInUsername;
            JSONObject userDetails = new JSONObject();
            userDetails.put("APN", apnWithSubtype); 
            userDetails.put("Type", type);
            userDetails.put("SuperType", supertype);
            userDetails.put("Description", description);
            userDetails.put("ResponsibleEngineer", responsibleEngineer);

            JSONObject result = createPartToExcel(userDetails, loggedInUsername);
            return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();

            
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("Status", "Failed");
            error.put("Message", "Error during creation: " + e.getMessage());
            return Response.ok(error.toString(), MediaType.APPLICATION_JSON).build();
        }
    }
    public String generateObjectIdFromName(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(name.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 8; i += 2) {
                int part = ((digest[i] & 0xFF) << 8) | (digest[i + 1] & 0xFF);
                sb.append(String.format("%04X", part)); 
                if (i < 6) {
                    sb.append(".");
                }
            }
	return sb.toString();
        } catch (Exception e) {
            return generateRandomFormattedObjectId();
        }
    }
 private String generateRandomFormattedObjectId() {
     SecureRandom random = new SecureRandom();
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < 4; i++) {
         int part = random.nextInt(0x10000);
         sb.append(String.format("%04X", part));
         if (i < 3) {
             sb.append(".");
         }
     }
     return sb.toString();
 }
 
 public JSONObject createPartToExcel(JSONObject userDetails, String loggedInUsername) throws IOException {
	    File file = new File(FILE_PATH);
	    Workbook workbook;
	    Sheet sheet;

	    if (file.exists()) {
	        try (FileInputStream fis = new FileInputStream(file)) {
	            workbook = new XSSFWorkbook(fis);
	        }
	        sheet = workbook.getSheet("MainDataBase");
	        if (sheet == null) {
	            sheet = workbook.createSheet("MainDataBase");
	            createHeaderRow(sheet); 
	        }
	    } else {
	        workbook = new XSSFWorkbook();
	        sheet = workbook.createSheet("MainDataBase");
	        createHeaderRow(sheet); 
	    }

	    String apnWithSubtype = userDetails.getString("APN"); 
	    String supertype = userDetails.getString("SuperType");
	    String type = userDetails.getString("Type");
	    String description = userDetails.getString("Description");
	    String createdDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
	    String owner = loggedInUsername;
	    String emailId = loggedInUsername + "@apn.com";

	    String[] apnParts = apnWithSubtype.split("-");
	    String baseApn = apnParts[0];  
	    String subtype = apnParts.length > 1 ? apnParts[1] : "";  
	    int nextNumber = getNextApnNumber(sheet, baseApn, subtype);

	    String fullApn = String.format("%s-%03d-APN", baseApn, nextNumber);
	    String apnField = String.format("%s-%s", baseApn, subtype);  

	    String objectId = generateRandomFormattedObjectId();

	    int lastRowNumber = sheet.getLastRowNum();
	    Row row = sheet.createRow(lastRowNumber + 1);
	    row.createCell(0).setCellValue(objectId);
	    row.createCell(1).setCellValue(apnField);  
	    row.createCell(2).setCellValue(fullApn);  
	    row.createCell(3).setCellValue(supertype);
	    row.createCell(4).setCellValue(type);
	    row.createCell(5).setCellValue(description);
	    row.createCell(6).setCellValue(createdDate);
	    row.createCell(7).setCellValue(owner);
	    row.createCell(8).setCellValue(emailId);

	    try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
	        workbook.write(fos);
	    }
	    workbook.close();
	    JSONObject response = new JSONObject();
	    response.put("Status", "Success");
	    response.put("Message", "Part saved successfully");
	    response.put("APN", apnField); 
	    response.put("Owner", owner);
	    response.put("CreatedDate", createdDate);
	    response.put("ObjectId", objectId);
	    return response;
	}






    public void createHeaderRow(Sheet sheet) {
        Row header = sheet.createRow(0);
        String[] headers = {"ObjectId", "APN", "Name", "SuperType", "Type","Description", "CreatedDate", "Owner","EmailId"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }
    }

    public int getNextApnNumber(Sheet sheet, String baseApn, String subtype) {
        int maxNumber = 0;
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell apnCell = row.getCell(1); 
                if (apnCell != null) {
                    String apn = apnCell.getStringCellValue();
                    if (apn.startsWith(baseApn + "-" + subtype)) {
                        String[] parts = apn.split("-");
                        if (parts.length >= 3) {
                            try {
                                int number = Integer.parseInt(parts[1]); 
                                maxNumber = Math.max(maxNumber, number);
                            } catch (NumberFormatException e) {
                                System.out.println("Error parsing number: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        return maxNumber + 1;
    }





    @POST
    @Path("/loadAll")
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAllTypesAndSubtypes() {
        JSONObject response = new JSONObject();
        try (FileInputStream fis = new FileInputStream(new File(FILE_PATH));
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet("MainDataBase");
            if (sheet == null) {
                response.put("SuperType", new JSONArray());
                response.put("Type", new JSONArray());
                response.put("SubType", new JSONArray());
                return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
            }

            JSONArray superTypes = new JSONArray();
            JSONArray types = new JSONArray();
            JSONArray subTypes = new JSONArray();
            java.util.Set<String> superTypeSet = new java.util.HashSet<>();
            java.util.Set<String> typeSet = new java.util.HashSet<>();
            java.util.Set<String> subTypeSet = new java.util.HashSet<>();
            int rows = sheet.getLastRowNum();
            for (int i = 1; i <= rows; i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell superTypeCell = row.getCell(3);
                    Cell typeCell = row.getCell(4);
                    Cell subTypeCell = row.getCell(5);
                    if (superTypeCell != null) {
                        String val = superTypeCell.getStringCellValue();
                        if (!val.isEmpty()) superTypeSet.add(val);
                    }
                    if (typeCell != null) {
                        String val = typeCell.getStringCellValue();
                        if (!val.isEmpty()) typeSet.add(val);
                    }
                    if (subTypeCell != null) {
                        String val = subTypeCell.getStringCellValue();
                        if (!val.isEmpty()) subTypeSet.add(val);
                    }
                }
            }
            superTypeSet.forEach(superTypes::put);
            typeSet.forEach(types::put);
            subTypeSet.forEach(subTypes::put);
            response.put("SuperType", superTypes);
            response.put("Type", types);
            response.put("SubType", subTypes);
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("Status", "Failed");
            error.put("Message", "Error reading Excel: " + e.getMessage());
            return Response.ok(error.toString(), MediaType.APPLICATION_JSON).build();        }
    }
    
    
    
}

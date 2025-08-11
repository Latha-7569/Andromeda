package org.navigator;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.poi.xssf.usermodel.*;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@Path("/myresource")
public class MyResource {

    private static final String FILE_PATH = "C:\\Users\\Public\\NikhilWorkspace\\WebserviceLearning\\RegistrationDetails.xlsx";

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(
            @FormParam("Email") String email,
            @FormParam("Username") String username,
            @FormParam("Firstname") String firstname,
            @FormParam("Lastname") String lastname,
            @FormParam("Password") String password,
            @FormParam("ConfirmPassword") String confirmPassword,
            @FormParam("Country") String country) {

        try {
            if (!password.equals(confirmPassword)) {
                JSONObject error = new JSONObject();
                error.put("Status", "Failed");
                error.put("Message", "Password and Confirm Password do not match");
                return Response.status(Response.Status.BAD_REQUEST).entity(error.toString(2)).build();
            }

            JSONObject userDetails = new JSONObject();
            userDetails.put("Email", email);
            userDetails.put("Username", username);
            userDetails.put("Firstname", firstname);
            userDetails.put("Lastname", lastname);
            userDetails.put("Password", password);
            userDetails.put("ConfirmPassword", confirmPassword);
            userDetails.put("Country", country);

            JSONObject result = registerUserToExcel(userDetails);
            return Response.ok(result.toString(), MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("Status", "Failed");
            error.put("Message", "Error during registration: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toString(2)).build();
        }
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response loginUser(
            @FormParam("Username") String username,
            @FormParam("Password") String password) {
        try {
            JSONObject response = new JSONObject();
            boolean matchFound = false;

            File file = new File(FILE_PATH);
            if (!file.exists()) {
                response.put("Status", "Failed");
                response.put("Message", "No users registered yet");
                return Response.status(Response.Status.BAD_REQUEST).entity(response.toString(2)).build();
            }

            try (FileInputStream fis = new FileInputStream(file);
                 XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

                XSSFSheet sheet = workbook.getSheet("Authentication");
                if (sheet == null) {
                    throw new Exception("Excel sheet 'Authentication' not found");
                }

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    XSSFRow row = sheet.getRow(i);
                    if (row == null) continue;

                    String storedUsername = getCellValue(row.getCell(1));
                    String storedPassword = getCellValue(row.getCell(4));

                    if (storedUsername.equals(username) && storedPassword.equals(password)) {
                        response.put("Status", "Success");
                        response.put("Message", "Login successful");
                        response.put("Email", getCellValue(row.getCell(0)));
                        response.put("Username", storedUsername);
                        response.put("Firstname", getCellValue(row.getCell(2)));
                        response.put("Lastname", getCellValue(row.getCell(3)));
                        response.put("Country", getCellValue(row.getCell(6)));
                        matchFound = true;
                        break;
                    }
                }
            }

            if (!matchFound) {
                response.put("Status", "Failed");
                response.put("Message", "Invalid username or password");
            }

            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getClass().getName());
            JSONObject error = new JSONObject();
            error.put("Status", "Failed");
            error.put("Message", "Login failed: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toString(2)).build();
        }
    }

    private JSONObject registerUserToExcel(JSONObject hmdetails) throws Exception {
        File file = new File(FILE_PATH);
        XSSFWorkbook workbook;
        XSSFSheet sheet;

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            }
        } else {
            workbook = new XSSFWorkbook();
        }

        sheet = workbook.getSheet("Authentication");
        if (sheet == null) {
            sheet = workbook.createSheet("Authentication");
            XSSFRow header = sheet.createRow(0);
            header.createCell(0).setCellValue("Email");
            header.createCell(1).setCellValue("Username");
            header.createCell(2).setCellValue("Firstname");
            header.createCell(3).setCellValue("Lastname");
            header.createCell(4).setCellValue("Password");
            header.createCell(5).setCellValue("ConfirmPassword");
            header.createCell(6).setCellValue("Country");
        }
        int newRowNum = sheet.getLastRowNum() + 1;
        XSSFRow newRow = sheet.createRow(newRowNum);
        newRow.createCell(0).setCellValue(hmdetails.optString("Email", ""));
        newRow.createCell(1).setCellValue(hmdetails.optString("Username", ""));
        newRow.createCell(2).setCellValue(hmdetails.optString("Firstname", ""));
        newRow.createCell(3).setCellValue(hmdetails.optString("Lastname", ""));
        newRow.createCell(4).setCellValue(hmdetails.optString("Password", ""));
        newRow.createCell(5).setCellValue(hmdetails.optString("ConfirmPassword", ""));
        newRow.createCell(6).setCellValue(hmdetails.optString("Country", ""));

        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            workbook.write(fos);
        }
        workbook.close();

        JSONObject result = new JSONObject();
        result.put("Status", "Success");
        result.put("Message", "User registered successfully");
        return result;
    }

    private String getCellValue(XSSFCell cell) {
        return (cell != null) ? cell.toString().trim() : "";
    }
    
    
}

package org.navigator;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import amd.DataFetchAMD;
import amd.DataFetchAMD.ObjectIdNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/datafetchservice")
public class DataFetchService {

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInfo( @QueryParam("objectId") String objectId, @QueryParam("field") String field){
        try {
            DataFetchAMD fetcher = new DataFetchAMD(objectId);
            String value = fetcher.getInfo(field);
            if (value != null) {
                return Response.ok("{\"value\":\"" + value + "\"}").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Field not found or empty\"}").build();
            }
        } catch (ObjectIdNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error reading data\"}").build();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllInfo(@QueryParam("objectId") String objectId) throws IOException {
        try {
            DataFetchAMD fetcher = new DataFetchAMD(objectId);
            Map<String, String> data = fetcher.getInfos();
            return Response.ok(data).build();
        } catch (ObjectIdNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
        }
    }
    @GET
    @Path("/latestparts")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestParts() {
        try {
            List<Map<String, String>> latestParts = DataFetchAMD.getLatestPartsFromDB();
            List<Map<String, String>> filteredParts = new ArrayList<>();
            for (Map<String, String> part : latestParts) {
                Map<String, String> filtered = new LinkedHashMap<>();
                filtered.put("ObjectId", part.get("ObjectId"));
                filtered.put("Name", part.get("Name"));
                filtered.put("APN", part.get("APN"));
                filtered.put("SuperType", part.get("SuperType"));
                filtered.put("Type", part.get("Type"));
                filtered.put("Description", part.get("Description"));
                filtered.put("CreatedDate", part.get("CreatedDate"));
                filtered.put("Owner", part.get("Owner"));
                filteredParts.add(filtered);
            }
            return Response.ok(filteredParts).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Failed to load latest parts.\"}").build();
        }
    }
    
    @DELETE
    @Path("/deleteObject")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteObject(@QueryParam("objectId") String objectId) {
    	if(objectId.isEmpty() || objectId==null) {
    		return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Missing or empty objectId parameter.\"}").build();
    	}
    	try {
    		DataFetchAMD fetcher=new DataFetchAMD(objectId);
    		fetcher.deleteObject();
    		return Response.ok("{\"message\":\"ObjectId '" + objectId + "' successfully deleted.\"}").build();    		
    	}catch(Exception e) {
    		return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"" + e.getMessage() + "\"}").build();
    		
    	}
    	
    }
    
    @POST
    @Path("/createobject")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createObject(
        @FormParam("SuperType") String supertype,
        @FormParam("Type") String type,
        @FormParam("APN") String apnWithSubtype,
        @FormParam("Description") String description,
        @FormParam("ResponsibleEngineer") String responsibleEngineerParam,
        @Context HttpServletRequest request) {

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
            String responsibleEngineer = (responsibleEngineerParam != null && !responsibleEngineerParam.isEmpty())
                    ? responsibleEngineerParam
                    : loggedInUsername;
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("SuperType", supertype);
            jsonObj.put("Type", type);
            jsonObj.put("APN", apnWithSubtype);
            jsonObj.put("Description", description);
            jsonObj.put("ResponsibleEngineer", responsibleEngineer);
            DataFetchAMD fetcher = new DataFetchAMD();
            fetcher.createObject(jsonObj.toString());
            JSONObject success = new JSONObject();
            success.put("Status", "Success");
            success.put("Message", "Object created successfully");
            return Response.ok(success.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("Status", "Failed");
            error.put("Message", "Internal server error: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error.toString()).build();
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
 
}


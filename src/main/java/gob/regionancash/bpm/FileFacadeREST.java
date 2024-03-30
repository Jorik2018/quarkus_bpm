package gob.regionancash.node.controller;



import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("file")
public class FileFacadeREST {

    @POST
    public Object get(Map m) {
        File directory = new File((String)m.get("folder"));
        ArrayList list = new ArrayList<>();
        // Get list of files and directories in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                list.add(Map.of("file", file.getAbsolutePath()));
            }
        }
        return Map.of("data", list);
    }

    @POST
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(Map m) {
        try {
            File file = new  File((String)m.get("folder"));
            FileInputStream fileInputStream = new FileInputStream(file);
            Response.ResponseBuilder responseBuilder = Response.ok(fileInputStream);
            responseBuilder.header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            return responseBuilder.build();
        } catch (FileNotFoundException e) {
            return Response.serverError().entity("Error downloading file: " + e.getMessage()).build();
        }
    }
    
}
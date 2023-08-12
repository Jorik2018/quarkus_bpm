package gob.regionancash.bpm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.isobit.app.service.UserService;
import org.isobit.util.XUtil;

import gob.regionancash.bpm.model.BpmProcessRun;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Resource {
	
	@Inject
	Service service;
	
    @Inject
    JsonWebToken jwt; 

    @GET
    @Path("prepare/{processId}")
    @PermitAll
    public Object prepare(@PathParam("processId") Integer processId){
        return service.prepare(processId);
    }
    
    @GET
    @Path("details/{runId}")
    @PermitAll
    public Object getDetails(@PathParam("runId") Integer runId){
        return service.getDetails(runId);
    }
    
    @GET
    @Path("test")
    @PermitAll
    public Object test(){
    	return jwt.getClaimNames()+" u="+jwt.containsClaim("uid")+"; "+(jwt.containsClaim("directory")?jwt.getClaim("directory"):"-");
    }
    
    @POST
    @Path("run")
    public Object postRun(BpmProcessRun run){
    	int uid=XUtil.intValue(jwt.getClaim("uid"));
    	int directory=jwt.containsClaim("directory")?XUtil.intValue(jwt.getClaim("directory")):0;
    	run.setPeopleId(directory>0?directory:-uid);
        return service.postRun(run);
    }
    
    @GET
    @Path("user")
    public Object getUserList(){
    	return service.getUserList();
    }
    
    @Inject
    UserService userService;
    
    @GET
    @Path("perms")
    public Object getPermList(){
    	return userService.getPermList();
    }
    
    @GET
    @Path("run/{from}/{to}")
    @PermitAll
    public Object getRun(@PathParam("from") Integer from, @PathParam("to") Integer to,
            @QueryParam("year") Integer year,
            @QueryParam("number") Integer number,
            @QueryParam("subject") String subject){
        HashMap m = new HashMap();
        if(year!=null)m.put("year", year);
        if(number!=null)m.put("number", number);
        if(subject!=null)m.put("subject", subject);
        m.put("data", service.load(from, to, null, m));
        System.out.println(m);
        return m;
    }
    
    @GET
    @Path("relation/{from}/{to}")
    public Object getRelation(@PathParam("from") Integer from, @PathParam("to") Integer to){
        HashMap m = new HashMap();
        m.put("data", service.loadRelation(from, to, null, m));
        return m;
    }
	
    
    @GET
    @Path("run/{id}")
    @PermitAll
    public Object getRun(@PathParam("id") Integer id){
        return service.getRun(id);
    }
    
    @GET
    @Path("dispatch/{id}")
    @PermitAll
    public Object getDispatch(@PathParam("id") Integer id){
        return service.getDispatch(id);
    }
    
    @DELETE
    @Path("run/{id}")
    @PermitAll
    public Object deleteRun(@PathParam("id") Integer id){
        return service.deleteRun(id);
    }
    
	@POST
    @Path("run/download")
    @PermitAll
	public Object download(Map args) {
		String fileName="ficha.pdf";
		args.put("-EXTENSION", "pdf");
		InputStream is2 = ClientBuilder.newClient().target("http://web.regionancash.gob.pe//admin/jasper/api/export/").request().get(InputStream.class);
		return Response.ok((StreamingOutput) (java.io.OutputStream output) -> {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int len;
				byte[] buffer = new byte[4096];
				while ((len = is2.read(buffer, 0, buffer.length)) != -1) {
					output.write(buffer, 0, len);
				}
				output.flush();
				is2.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new WebApplicationException("File Not Found !!", e);
			}
		}, MediaType.APPLICATION_OCTET_STREAM).header("content-disposition", "attachment; filename = " + fileName)
				.build();
	}
    

}
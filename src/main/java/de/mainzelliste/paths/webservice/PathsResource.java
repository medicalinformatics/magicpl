package de.mainzelliste.paths.webservice;

import java.util.List;
import java.util.Map;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.Gson;

import de.mainzelliste.paths.backend.Controller;
import de.mainzelliste.paths.backend.PathBackend;
import de.mainzelliste.paths.processor.AbstractProcessor;

@Path("/paths")
public class PathsResource {

	private PathBackend backend = Controller.instance.getPathBackend();
	private Gson gson = new Gson();
	
	
	@POST
	@Path("/{pathName}")
	public Response executePath(@PathParam("pathName") String pathName, String data) {
		AbstractProcessor implementation = backend.getPathImplementation(pathName);
		if (implementation == null)
			throw new WebApplicationException(
					Response.status(Status.NOT_IMPLEMENTED).entity("Path " + pathName + " not implemented!").build());
		
		Map<String, Object> inputMap = backend.unmarshal(data);
		inputMap = backend.filterPathInput(pathName, inputMap);
		
		Map<String, Object> outputMap = implementation.apply(inputMap);
		String outputData = backend.marshal(outputMap);
		
		return Response.ok(outputData).build();
	}

	/**
	 * Output information on which paths are provided by this instance.
	 * 
	 * @return Information on paths (currently only names).
	 */
	@OPTIONS
	public Response getPathInformation() {
		/*
		 * Perspektivisch sollten hier genauere, strukturierte Informationen
		 * zurückgegeben werden, z.B. welche Ein- und Ausgaben die Pfade haben
		 * etc. Möglichst nur die anzeigen, die der zugreifende Client auch
		 * benutzen darf.
		 */
		return Response.ok(Controller.instance.getPathBackend().getPathNames().toString()).build();
	}
}

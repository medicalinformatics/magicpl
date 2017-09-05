package de.mainzelliste.paths.webservice;

import java.util.function.Function;

import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.mainzelliste.paths.backend.Controller;

@Path("/paths")
public class PathsResource {
	
	@POST
	@Path("/{pathName}")
	public Response executePath(@PathParam("pathName") String pathName, String data) {
		Function<String, String> implementation = Controller.instance.getPathBackend().getPathImplementation(pathName);
		if (implementation == null)
			throw new WebApplicationException(
					Response.status(Status.NOT_IMPLEMENTED).entity("Path " + pathName + " not implemented!").build());
		return Response.ok(implementation.apply(data)).build();
	}
	
	/** Output information on which paths are provided by this instance.
	 * 
	 * @return Information on paths (currently only names). */
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
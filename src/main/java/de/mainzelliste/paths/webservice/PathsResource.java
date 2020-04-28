package de.mainzelliste.paths.webservice;

import de.mainzelliste.paths.backend.Controller;
import de.mainzelliste.paths.backend.PathBackend;
import de.mainzelliste.paths.processor.AbstractProcessor;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/paths")
public class PathsResource {

    private final PathBackend backend = Controller.getPathBackend();

    @POST
    @Path("/{pathName}")
    public Response executePath(@Context HttpServletRequest req, @PathParam("pathName") String pathName, String data) {
        Controller.authenticator.checkPermission(req, pathName);
        AbstractProcessor implementation = backend.getPathImplementation(pathName);
        if (implementation == null)
            throw new WebApplicationException(
                    Response.status(Status.NOT_IMPLEMENTED).entity("Path " + pathName + " not implemented!").build());

        // Unmarshal input data and check if restrictions on input are met
        Map<String, String> rawInput = backend.unmarshalToStrings(data);
        Controller.authenticator.checkRestrictions(req, pathName, rawInput);
        Map<String, Object> inputMap = backend.unmarshal(rawInput);
        inputMap = backend.filterPathInput(pathName, inputMap);

        Map<String, Object> outputMap;
        outputMap = implementation.apply(inputMap);

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
        return Response.ok(Controller.getPathBackend().getPathNames().toString()).build();
    }
}

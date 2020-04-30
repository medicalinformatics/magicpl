package de.mainzelliste.paths.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebAppExceptionMapper implements ExceptionMapper<WebApplicationException> {
    @Override
    public Response toResponse(WebApplicationException exception) {
        return !exception.getResponse().hasEntity() ? Response.fromResponse(exception.getResponse())
                .entity(exception.getMessage()).build() : exception.getResponse();
    }
}
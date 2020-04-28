package de.mainzelliste.paths.utils;

import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

public class HttpUtils {

    private HttpUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert old sun ClientResponse to JAX-RS Client
     *
     * @param oldResponse
     *            ClientResponse
     * @return JAX-RS Client
     */
    public static Response convertToResponse(ClientResponse oldResponse) {
        ResponseBuilder builder = Response.status(oldResponse.getStatus());
        if (oldResponse.getHeaders().containsKey("location")) {
            builder.header("location", oldResponse.getHeaders().getFirst("location"));
        }
        builder.entity(oldResponse.getEntity(String.class));
        return builder.build();
    }
}

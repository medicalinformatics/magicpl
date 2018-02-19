package de.mainzelliste.paths.processor;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import de.mainzelliste.paths.adapters.Adapter;
import de.mainzelliste.paths.adapters.AdapterFactory;
import de.mainzelliste.paths.backend.Controller;
import de.mainzelliste.paths.configuration.ConfigurationBackend;
import de.mainzelliste.paths.configuration.Path;
import de.pseudonymisierung.mainzelliste.client.*;
import de.samply.common.config.Configuration;
import de.samply.common.http.HttpConnector;
import de.samply.common.http.HttpConnectorException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainzellisteClient extends AbstractProcessor {
    private final Client webClient;
    private MainzellisteConnection mainzellisteConnection;

    /**
     * Default constructor. Initializes name (see {@link #getPathName()}) and
     * parameter map (see {@link #getParameters()}) from the given
     * configuration.
     *
     * @param configuration Configuration of this path.
     */
    public MainzellisteClient(Path configuration) {
        super(configuration);

        String mainzellisteUrl = this.getParameters().get("mainzellisteURL").getValue();
        String mainzellisteApiKey = this.getParameters().get("mainzellisteApiKey").getValue();

        try {
            // TODO: Proxy hinzufügen
//            HttpConnector httpConnector = new HttpConnector(proxy);
            HttpConnector httpConnector = new HttpConnector();
            mainzellisteConnection = new MainzellisteConnection(mainzellisteUrl.toString(), mainzellisteApiKey, httpConnector.getHttpClient(mainzellisteUrl));
            webClient = httpConnector.getJerseyClientForHTTPS();
        } catch (URISyntaxException e) {
//            logger.fatal("Invalid URI for Mainzelliste connection: " + mainzellisteUrl);
            throw new Error("Invalid URI for Mainzelliste connection: " + mainzellisteUrl);
//        } catch (HttpConnectorException e) {
//            e.printStackTrace();
//            throw new Error("Cannot connect to Mainzelliste");
        }
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> stringObjectMap) {
        Map<String, Object> output = new HashMap<>();
        String uriString = mainzellisteConnection.getMainzellisteURI() + "patients?tokenId=" + stringObjectMap.get("KNTKT");

        URI idUri;

        try {
            idUri = new URI(uriString);
        } catch (URISyntaxException e) {
//            logger.error("Invalid URI for ID request: " + uriString, e);
            throw new Error("A request used an invalid URI. See log for details.");
        }

        if (idUri != null) {
            WebResource idResource = webClient.resource(idUri);
//            logger.info("Requesting ID: POST " + idUri);
            ClientResponse response = idResource
                    .accept(MediaType.APPLICATION_JSON)
                    .header("mainzellisteApiVersion", "2.0")
                    .type(MediaType.APPLICATION_FORM_URLENCODED)
                    .post(ClientResponse.class);
            if (response.getStatus() >= 400) {
                throw new WebApplicationException(
                        Response.status(response.getStatus())
                                .entity("Request to Mainzelliste failed with: " + response.getEntity(String.class))
                                .build());
            }

            try {
                List<Map<String, Object>> responseList = response.getEntity(new GenericType<List<Map<String, Object>>>() {});
                Map<String, Object> responseMap = responseList.get(0);
                String id = responseMap.get("idString").toString();
                String idType = responseMap.get("idType").toString();
                output.put("idType", idType);
                output.put("idString", id);
                return output;

//                for (Map<String, Object> thisId : responseList) {
//                    if (this.getMainzellisteIdType(idType).equals(thisId.get("idType")))
//                        thisResponse.put(idType, (String) thisId.get("idString"), (Boolean) thisId.get("tentative"));
//                }
//                logger.info(responseList.toString());
            } catch (Throwable t) {
//                logger.error("IdManager caught Exception...", t);
                throw new WebApplicationException(t, Response.Status.INTERNAL_SERVER_ERROR);
            } finally {
                response.close();
            }
        } else {
            //TODO What is to happen if the URI is null?
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get the Mainzelliste session for this instance. The session will be recreated if invalid (i.e.
     * due to timing out).
     *
     * @return The Mainzelliste session.
     * @throws MainzellisteNetworkException
     */
    private Session getSession() throws MainzellisteNetworkException {
        Session mainzellisteSession = mainzellisteConnection.createSession();
        return mainzellisteSession;
    }
}

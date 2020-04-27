package de.mainzelliste.paths.processor;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import de.mainzelliste.paths.configuration.Path;
import de.pseudonymisierung.controlnumbers.ControlNumber;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MainzellisteClient extends AbstractProcessor {
    private final String SURENESS_FLAG = "sureness";
    private final Client webClient;
    private final String mainzellisteUrl;

    /**
     * Default constructor. Initializes name (see {@link #getPathName()}) and
     * parameter map (see {@link #getParameters()}) from the given
     * configuration.
     *
     * @param configuration
     *            Configuration of this path.
     */
    public MainzellisteClient(Path configuration) {
        super(configuration);

        String mainzellisteUrlParam = this.getParameters().get("mainzellisteURL").getValue();
        this.mainzellisteUrl =
                mainzellisteUrlParam + (mainzellisteUrlParam.endsWith("/") ? "" : "/");
        // check url of maizelliste
        try {
            new URI(mainzellisteUrl);
        } catch (URISyntaxException e) {
            throw new WebApplicationException("Invalid Mainzelliste Url: " + mainzellisteUrl);
        }

        webClient = Client.create();
        webClient.setFollowRedirects(false);
    }

    @Override
    public Map<String, Object> process(Map<String, Object> stringObjectMap) {
        Map<String, Object> output = new HashMap<>();

        URI idUri;
        try {
            idUri = new URI(mainzellisteUrl + "patients?tokenId=" + stringObjectMap.get("tokenId"));
        } catch (URISyntaxException e) {
            throw new ProcessingException("Invalid Mainzelliste uri", e);
        }

        // prepare form data
        MultivaluedMap<String, String> formData = new MultivaluedHashMap<>(
            stringObjectMap.entrySet().stream()
                .filter(e -> e.getValue() instanceof ControlNumber)
                .collect(Collectors.toMap(Map.Entry::getKey,
                    e -> createJsonFrom(e.getKey(), (ControlNumber) e.getValue())))
        );
        // add sureness flag if exist
        if(stringObjectMap.containsKey(SURENESS_FLAG) &&
            Boolean.parseBoolean((String)stringObjectMap.get(SURENESS_FLAG))) {
            formData.add(SURENESS_FLAG, "true");
        }

        WebResource idResource = webClient.resource(idUri);
        ClientResponse response = idResource
            .accept(MediaType.APPLICATION_JSON)
            .header("mainzellisteApiVersion",
                this.getParameters().get("mainzellisteApiVersion").getValue())
            .type(MediaType.APPLICATION_FORM_URLENCODED)
            .post(ClientResponse.class, formData);
        if (response.getStatus() == 409) {
            throw new WebApplicationException(
                Response.status(response.getStatus())
                    .entity(response.getEntity(String.class))
                    .build());
        }
        else if (response.getStatus() >= 400) {
            throw new WebApplicationException(
                Response.status(response.getStatus())
                    .entity(
                        "Request to Mainzelliste failed with: " + response.getEntity(String.class))
                    .build());
        } else if (response.getStatus() == 303) {
            output.put("redirect", response.getHeaders().get("location"));
            return output;
        }

        try {
            List<Map<String, Object>> responseList = response
                .getEntity(new GenericType<List<Map<String, Object>>>() {
                });
            Map<String, Object> responseMap = responseList.get(0);
            output.put("idType", responseMap.get("idType").toString());
            output.put("idString", responseMap.get("idString").toString());
            return output;
        } catch (RuntimeException exception) {
            throw new WebApplicationException("Invalid Mainzelliste response body", exception);
        } finally {
            response.close();
        }
    }

    private String createJsonFrom(String key, ControlNumber value) {
        try {
            JSONObject json = new JSONObject();
            json.put("keyId", key.substring(0, key.length() - 2));
            json.put("value", value.toBitString());
            return json.toString();
        } catch (JSONException e) {
            return "";
        }
    }
}

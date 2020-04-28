package de.mainzelliste.paths.processor;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.utils.HttpUtils;
import de.pseudonymisierung.controlnumbers.ControlNumber;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class MainzellisteClient extends AbstractProcessor {

    private static final String SURENESS_FLAG = "sureness";
    private static final String ML_URL_PARAM = "mainzellisteURL";
    private static final String ML_API_VERSION_PARAM = "mainzellisteApiVersion";

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

      String mainzellisteUrlParam = this.getParameters().get(ML_URL_PARAM).getValue();
      this.mainzellisteUrl =
          mainzellisteUrlParam + (mainzellisteUrlParam.endsWith("/") ? "" : "/");
      // check url of maizelliste
      try {
        new URI(mainzellisteUrl);
      } catch (URISyntaxException e) {
        throw new WebApplicationException("Invalid Mainzelliste Url: " + mainzellisteUrl);
      }

        // POJO JSON support client configuration
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        webClient = Client.create(clientConfig);
        webClient.setFollowRedirects(false);
    }

    @Override
  public Map<String, Object> process(Map<String, Object> stringObjectMap) {
    // add token id to maizelliste url
    URI mainzellisteUri;
    try {
      mainzellisteUri = new URI(mainzellisteUrl + "patients?tokenId=" + stringObjectMap.get("tokenId"));
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
    if (stringObjectMap.containsKey(SURENESS_FLAG) &&
        Boolean.parseBoolean((String) stringObjectMap.get(SURENESS_FLAG))) {
      formData.add(SURENESS_FLAG, "true");
    }

    // execute http request
    ClientResponse response = webClient
        .resource(mainzellisteUri)
        .accept(MediaType.APPLICATION_JSON)
        .header(ML_API_VERSION_PARAM,
            this.getParameters().get(ML_API_VERSION_PARAM).getValue())
        .type(MediaType.APPLICATION_FORM_URLENCODED)
        .post(ClientResponse.class, formData);

    // redirect error response
    if (response.getStatus() >= 400) {
      throw new WebApplicationException(HttpUtils.convertToResponse(response));
    } else if (response.getStatus() == 303) {
      throw new RedirectionException(HttpUtils.convertToResponse(response));
    }

    // prepare result
    try {
      Map<String, Object> output = new HashMap<>();
      List<Map<String, Object>> responseList = response
          .getEntity(new GenericType<List<Map<String, Object>>>(){});
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

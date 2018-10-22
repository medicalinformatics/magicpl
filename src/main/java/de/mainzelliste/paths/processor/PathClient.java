package de.mainzelliste.paths.processor;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import de.mainzelliste.paths.backend.Controller;
import de.mainzelliste.paths.backend.PathBackend;
import de.mainzelliste.paths.configuration.Path;
import de.pseudonymisierung.mainzelliste.client.MainzellisteConnection;
import de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException;
import de.pseudonymisierung.mainzelliste.client.Session;
import de.samply.common.http.HttpConnector;
import de.samply.common.http.HttpConnectorException;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathClient extends AbstractProcessor {
    private final String url;
    private final String method;
    private MainzellisteConnection mainzellisteConnection;

    /**
     * Default constructor. Initializes name (see {@link #getPathName()}) and
     * parameter map (see {@link #getParameters()}) from the given
     * configuration.
     *
     * @param configuration
     *            Configuration of this path.
     */
    public PathClient(Path configuration) {
        super(configuration);

        url = this.getParameters().get("url").getValue();
        method = this.getParameters().get("method").getValue();
        // String mainzellisteApiKey =
        // this.getParameters().get("mainzellisteApiKey").getValue();

    }

    @Override
    public Map<String, Object> process(Map<String, Object> stringObjectMap) {
        String json = Controller.getPathBackend().marshal(stringObjectMap);
        HttpConnector hc = Controller.getHttpConnector();
        HashMap<String, Object> data = null;
        try {
            data = hc.doActionHashMap(method, url, null, null, "application/json", json, false,
                    false);
        } catch (HttpConnectorException e) {
            e.printStackTrace();
        }
        Map<String, Object> result = Controller.getPathBackend().unmarshal(data.get("body").toString());
        return result;
    }

    /**
     * Get the Mainzelliste session for this instance. The session will be
     * recreated if invalid (i.e. due to timing out).
     *
     * @return The Mainzelliste session.
     * @throws MainzellisteNetworkException
     */
    private Session getSession() throws MainzellisteNetworkException {
        Session mainzellisteSession = mainzellisteConnection.createSession();
        return mainzellisteSession;
    }
}

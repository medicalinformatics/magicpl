package de.mainzelliste.paths.processor;

import de.mainzelliste.paths.adapters.Adapter;
import de.mainzelliste.paths.adapters.AdapterFactory;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.utils.StringNormalizer;
import de.pseudonymisierung.mainzelliste.client.*;
import de.samply.common.http.HttpConnector;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.crypto.dsig.Transform;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class MainzellisteKNTicketClient extends AbstractProcessor {
    private MainzellisteConnection mainzellisteConnection;
    /**
     * Default constructor. Initializes name (see {@link #getPathName()}) and
     * parameter map (see {@link #getParameters()}) from the given
     * configuration.
     *
     * @param configuration Configuration of this path.
     */
    public MainzellisteKNTicketClient(Path configuration) {
        super(configuration);

        String mainzellisteUrl = this.getParameters().get("mainzellisteURL").getValue();
        String mainzellisteApiKey = this.getParameters().get("mainzellisteApiKey").getValue();

        try {
            HttpConnector httpConnector = new HttpConnector();
            mainzellisteConnection = new MainzellisteConnection(mainzellisteUrl.toString(), mainzellisteApiKey, httpConnector.getHttpClient(mainzellisteUrl));
        } catch (URISyntaxException e) {
//            logger.fatal("Invalid URI for Mainzelliste connection: " + mainzellisteUrl);
            throw new Error("Invalid URI for Mainzelliste connection: " + mainzellisteUrl);
        }
    }

    @Override
    public Map<String, Object> process(Map<String, Object> stringObjectMap) {
        AddPatientToken token = new AddPatientToken();

        try {
            for (String fieldName : stringObjectMap.keySet()) {
                Adapter adapter = AdapterFactory.getAdapter(stringObjectMap.get(fieldName).getClass());
                String fieldValue = adapter.marshal(stringObjectMap.get(fieldName));

                if (fieldValue == null || fieldValue.equals("")) {
                    token.addField(fieldName, "");
                }else if(fieldName.equals("IDType")){
                    // Add study identifier (=ID type) to fields
                    String controlNumberType = fieldValue.split("_")[0];
                    token.addField("study", controlNumberType);
                    // Add ID Type to token
                    token.addIdType(fieldValue);
                    // TODO: Schönere Lösung
                } else if(fieldName.equals("locallyUniqueIdEnc")) {
                    token.addField("locallyUniqueId", fieldValue);
                } else
                {
                    JSONObject thisControlNumber = new JSONObject();
                    // TODO: Schönere Lösung für Feldnamen
                    thisControlNumber.put("keyId", fieldName.substring(0, fieldName.length()-2));
                    thisControlNumber.put("value", fieldValue);
                    token.addField(fieldName.substring(0, fieldName.length()-2), thisControlNumber.toString());
                }
            }

            // TODO: Berücksichtigen locallyUniqueId und sureness

        } catch (JSONException e) {
//            logger.error("Error while packing control numbers as JSON", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
//            logger.error("An Exception occured: ", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        String tokenId;
        // Create Token on Mainzelliste
        try {
            tokenId = this.getSession().getToken(token);
        } catch (MainzellisteNetworkException e) {
//            logger.error("Network error on token creation", e);
            throw new Error(e);
        } catch (InvalidSessionException e) {
            // try one more time
            try {
                // getSessions recreates the session if needed
                tokenId = this.getSession().getToken(token);
            } catch (Throwable t) {
//                logger.error("Creating token failed on second attempt", t);
                throw new Error(e);
            }
        }
        Map<String, Object> output = new HashMap();
        output.put("KNTKT", tokenId);

        return output;
    }

    /**
     * Get the Mainzelliste session for this instance. The session will be recreated if invalid (i.e.
     * due to timing out).
     * @return The Mainzelliste session.
     * @throws de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException
     */
    private Session getSession() throws de.pseudonymisierung.mainzelliste.client.MainzellisteNetworkException {
        Session mainzellisteSession = mainzellisteConnection.createSession();
        return mainzellisteSession;
    }
}

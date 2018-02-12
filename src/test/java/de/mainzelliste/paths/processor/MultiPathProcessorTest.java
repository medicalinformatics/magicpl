package de.mainzelliste.paths.processor;

import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.WebAppDescriptor;
import de.mainzelliste.paths.configuration.Pathconfig;
import de.mainzelliste.paths.webservice.PathsResource;
import de.samply.config.util.JAXBUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import java.nio.file.Files;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.test.framework.JerseyTest;

import static org.testng.Assert.*;

public class MultiPathProcessorTest extends JerseyTest {

    public MultiPathProcessorTest() {
        super(setUpTest());
    }

    private static final String testFile = "/testAdapter.json";
    private static final String testConfig = "/testMultipathConfig.xml";
    private static Pathconfig configuration;
    private static String testData;

    private static final String packagePath = "de.mainzelliste.paths.webservice";
    private static final String configPackagePath = "de.mainzelliste.ConfigurationFile";
    private static final String configFile = "";

    @BeforeClass
    public void init() throws Exception{
        InputStream testConfiguration = this.getClass().getResourceAsStream(testConfig);
        testData = new String(Files.readAllBytes(java.nio.file.Paths.get(this.getClass().getResource(testFile).toURI())));
        if (testConfiguration == null)
            fail("Test configuration file " + testConfig + " not found.");

        try {
            configuration = JAXBUtil.unmarshall(testConfiguration, JAXBContext.newInstance(de.mainzelliste.paths.configuration.ObjectFactory.class), Pathconfig.class);
        } catch (Exception e) {
            fail("Error while unmarshalling configuration file", e);
        }
    }


    @Test
    public void testApply() throws Exception {
        WebResource resource = resource();
        ClientResponse testResponse;
        WebResource.Builder builder = resource.getRequestBuilder();
        resource.path("/getLocalId");

        testResponse = builder.post(ClientResponse.class);

        System.out.println(resource.getURI());
        System.out.println(testResponse);
        assertEquals(testResponse.getStatus(), 200);
    }

    /**
     * Set test configurations.
     *
     * @return should given the super constructor of an jersy test
     */
    public static AppDescriptor setUpTest () {
        System.setProperty("derby.stream.error.field", "java.lang.System.err");
        return new WebAppDescriptor.Builder(packagePath).contextParam(configPackagePath, configFile).contextPath("/paths/paths/getLocalId").build();
    }

}
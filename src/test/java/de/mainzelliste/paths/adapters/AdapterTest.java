package de.mainzelliste.paths.adapters;

import de.mainzelliste.paths.configuration.*;
import de.samply.config.util.JAXBUtil;
import jersey.repackaged.com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import java.nio.file.Files;

import static org.testng.Assert.*;

public class AdapterTest {

    private static final String testFile = "/testAdapter.json";
    private static final String testConfig = "/testAdapterConfig.xml";

    @Test
    public void testAdapter() throws Exception{
        InputStream testConfiguration = this.getClass().getResourceAsStream(testConfig);
        String test = new String(Files.readAllBytes(java.nio.file.Paths.get(this.getClass().getResource(testFile).toURI())));
        if (testConfiguration == null)
            fail("Test configuration file " + testConfig + " not found.");

        Pathconfig configuration;

        try {
            configuration = JAXBUtil.unmarshall(testConfiguration, JAXBContext.newInstance(de.mainzelliste.paths.configuration.ObjectFactory.class), Pathconfig.class);
        } catch (Exception e) {
            fail("Error while unmarshalling configuration file", e);
            return;
        }

        for (Path path : configuration.getPaths().getPathOrMultipath()) {
            for (Object io : path.getInput().getIosingleOrIorecord()) {
                if(io instanceof Iorecordref){
                    Iorecordref iorecordref = (Iorecordref) io;

                    if(!"IDAT".equals(iorecordref.getRef())){
                        continue;
                    }

                    Iorecord iorecord = null;

                    for (Object o : configuration.getIodefinitions().getIosingleOrIorecord()) {
                        if(o instanceof Iorecord && ((Iorecordref) io).getRef().equals(((Iorecord) o).getName())){
                            iorecord = (Iorecord) o;
                            break;
                        }
                    }

                    ImmutableMapAdapter adapter = (ImmutableMapAdapter) AdapterFactory.getAdapter(iorecord);
                    ImmutableMap<String, Object> map = adapter.unmarshal(test);



                    assertTrue("Klaus".equals(map.get("vorname")) && "Klausson".equals(map.get("nachname")) && "12.12.1212".equals(
                            map.get("geburtsdatum")));


                    String test2 = adapter.marshal(map);

                    ImmutableMap map2 = adapter.unmarshal(test2);

                    assertTrue(map.equals(map2));
                }
            }
        }
    }

}
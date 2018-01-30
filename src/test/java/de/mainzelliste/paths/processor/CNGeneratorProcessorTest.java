package de.mainzelliste.paths.processor;

import de.mainzelliste.paths.adapters.Adapter;
import de.mainzelliste.paths.adapters.AdapterFactory;
import de.mainzelliste.paths.adapters.ImmutableMapAdapter;
import de.mainzelliste.paths.configuration.*;
import de.samply.config.util.JAXBUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import java.io.InputStream;
import java.nio.file.Files;

import static org.testng.Assert.*;

public class CNGeneratorProcessorTest {
    private static final String testFile = "/testAdapter.json";
    private static final String testConfig = "/testAdapterConfig.xml";
    private static Pathconfig configuration;
    private static String testData;

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
    public void testCNGenerator() throws Exception{
        Path path = null;

        //Finding the needed path
        for (Path configPath : configuration.getPaths().getPathOrMultipath()) {
            // Just for testing as this is the only path implemented yet.
            if ("getCN".equals(configPath.getName())) {
                path = configPath;
                break;
            }
        }

        if(path == null) {
            fail("Path not found");
        }

        //TODO support for multipath missing
        AbstractProcessor processor = ProcessorFactory.getProcessor((SimplePath)path);

        //TODO multiple inputs
        //Finding the iorecord of the input.
        Iorecord iorecord = null;
        for (Object io : path.getInput().getIosingleOrIorecord()) {
            if (io instanceof Iorecordref) {
                Iorecordref iorecordref = (Iorecordref) io;
                for (Object o : configuration.getIodefinitions().getIosingleOrIorecord()) {
                    if (o instanceof Iorecord && ((Iorecordref) io).getRef().equals(((Iorecord) o).getName())) {
                        iorecord = (Iorecord) o;
                        break;
                    }
                }
            }
        }

        if(iorecord == null) {
            fail("No record found");
        }

        ImmutableMapAdapter adapter = (ImmutableMapAdapter) AdapterFactory.getAdapter(iorecord);

        Object output = processor.apply(adapter.unmarshal(testData));

        //TODO multiple outputs
        //Finding the iorecord of the output.
        for (Object io : path.getOutput().getIosingleOrIorecord()) {
            if (io instanceof Iorecordref) {
                Iorecordref iorecordref = (Iorecordref) io;
                for (Object o : configuration.getIodefinitions().getIosingleOrIorecord()) {
                    if (o instanceof Iorecord && ((Iorecordref) io).getRef().equals(((Iorecord) o).getName())) {
                        iorecord = (Iorecord) o;
                        break;
                    }
                }
            }
        }

        Adapter outputAdapter = AdapterFactory.getAdapter(iorecord);

        System.out.println(outputAdapter.marshal(output));

    }

}
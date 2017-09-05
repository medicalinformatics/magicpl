package de.mainzelliste.paths.test;

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import de.mainzelliste.paths.configuration.ParameterMap;
import de.mainzelliste.paths.configuration.Paths;
import de.mainzelliste.paths.configuration.Paths.Path;
import de.mainzelliste.paths.configuration.Paths.Path.Parameters.Parameter;
import de.samply.config.util.JAXBUtil;

/** Test for reading path parameters from configuration file. Needs
 * src/test/resources/testParameters.xml as input file. */
public class PathParameterTest {
	
	private static final String testConfigurationFile = "/testParameters.xml";
	
	@Test
	public void testPathParameters() {
		InputStream testConfiguration = this.getClass().getResourceAsStream(testConfigurationFile);
		if (testConfiguration == null)
			fail("Test configuration file " + testConfigurationFile + " not found.");
		
		Paths configuration;
		try {
			configuration = JAXBUtil.unmarshall(testConfiguration,
					JAXBContext.newInstance(de.mainzelliste.paths.configuration.ObjectFactory.class), Paths.class);
		} catch (Exception e) {
			fail("Error while unmarshalling configuration file", e);
			return;
		}
		
		for (Path thisPath : configuration.getPaths()) {
			if (thisPath.getName().equals("pathWithParameters")) {
				ParameterMap thisPathParameters = thisPath.getParameters();
				for (int i = 1; i <= 2; i++) {
					String thisParameterName = "param" + i;
					assertTrue(thisPathParameters.containsKey(thisParameterName),
							"Excpected parameter " + thisParameterName + " not found.");
					Parameter thisParameter = thisPathParameters.get(thisParameterName);
					assertEquals(thisParameter.getName(), thisParameterName,
							"Name in parameter does not match map key.");
					String thisParameterValue = thisParameter.getValue();
					assertEquals(thisParameterValue, "valueOfParam" + i,
							"Unexpected value for parameter " + thisParameterName);
				}
			} else if (thisPath.getName().equals("pathWithoutParameters")
					|| thisPath.getName().equals("pathWithEmptyParameters")) {
				assertNull(thisPath.getParameters(), "Unexpected parameter list for path without parameters.");
			} else {
				fail("Unexpected path in test configuration: " + thisPath.getName());
			}
		}
	}
}

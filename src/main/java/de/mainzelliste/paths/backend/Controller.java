package de.mainzelliste.paths.backend;

import java.io.File;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;

import de.mainzelliste.paths.configuration.ConfigurationBackend;
import de.mainzelliste.paths.configuration.Pathconfig;
import de.samply.config.util.JAXBUtil;

public enum Controller {

	instance;

	private PathBackend pathBackend;
	private ConfigurationBackend configurationBackend;

	// Initialize Application
	private Controller() {
		// read configuration
		try { // TODO Fehler speichern, Applikation starten und bei Zugriffen
				// Fehlermeldung ausgeben (z.B. per Filter?)
				// Konfiguration könnte wie folgt per samply.common.config
				// geladen werden:
				// File file = FileFinderUtil.findFile("pathsExample.xml",
				// "mainzelliste.paths", new File(".").getAbsolutePath());
				// Zu Testzwecken kommt die Datei direkt aus der Applikation:
			InputStream configStream = getClass().getClassLoader().getResourceAsStream("pathsExample.xml");
			
			// XML wird in Java-Objekt gelesen -> einfacher Zugriff auf die
			// Konfiguration
			Pathconfig configuration = JAXBUtil.unmarshall(configStream,
					JAXBContext.newInstance(de.mainzelliste.paths.configuration.ObjectFactory.class), Pathconfig.class);
			this.pathBackend = new PathBackend(configuration);
			this.configurationBackend = new ConfigurationBackend(configuration);
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	public PathBackend getPathBackend() {
		return pathBackend;
	}
	
	public ConfigurationBackend getConfigurationBackend() {
		return configurationBackend;
	}
}

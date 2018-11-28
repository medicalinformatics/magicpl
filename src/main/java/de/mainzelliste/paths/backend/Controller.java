package de.mainzelliste.paths.backend;

import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.xml.bind.JAXBContext;

import de.mainzelliste.paths.configuration.ConfigurationBackend;
import de.mainzelliste.paths.configuration.Pathconfig;
import de.samply.common.config.Configuration;
import de.samply.common.config.ObjectFactory;
import de.samply.common.http.HttpConnector;
import de.samply.config.util.JAXBUtil;

public class Controller {

	public static final PathBackend pathBackend;
	public static final ConfigurationBackend configurationBackend;
	public static final Authenticator authenticator;
	private static final HttpConnector httpConnector;

	// Initialize Application
	static {
		// read configuration
		try { // TODO Fehler speichern, Applikation starten und bei Zugriffen
				// Fehlermeldung ausgeben (z.B. per Filter?)
				// Konfiguration könnte wie folgt per samply.common.config
				// geladen werden:
				// File file = FileFinderUtil.findFile("pathsExample.xml",
				// "mainzelliste.paths", new File(".").getAbsolutePath());
				// Zu Testzwecken kommt die Datei direkt aus der Applikation:
			InputStream configStream = Controller.class.getClassLoader().getResourceAsStream("pathsDKTK.xml");
			InputStream proxyStream = Controller.class.getClassLoader().getResourceAsStream("proxy.xml");
			// XML wird in Java-Objekt gelesen -> einfacher Zugriff auf die
			// Konfiguration
			Pathconfig configuration = JAXBUtil.unmarshall(configStream,
					JAXBContext.newInstance(de.mainzelliste.paths.configuration.ObjectFactory.class), Pathconfig.class);
			Configuration proxy = JAXBUtil.unmarshall(proxyStream, JAXBContext.newInstance(ObjectFactory.class),
					Configuration.class);
			/*
			 * The order of these initializations is important, because
			 * PathBackend() and Authenticator() rely on configurationBackend
			 * and httpConnector being initialized.
			 */
			configurationBackend = new ConfigurationBackend(configuration, proxy);
			httpConnector = new HttpConnector(proxy);
			pathBackend = new PathBackend(configuration);
			authenticator = new Authenticator(configuration.getAuthentication());
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	public static PathBackend getPathBackend() {
		return pathBackend;
	}

	public static ConfigurationBackend getConfigurationBackend() {
		return configurationBackend;
	}

	/**
	 * Get a properly configured HttpConnector instance.
	 *
	 * @return The single application-wide HttpConnector instance. Uses the
	 *         proxy configuration from file 'proxy.xml', accessed via
	 *         samply.common.config.
	 */
	public static HttpConnector getHttpConnector() {
		return httpConnector;
	}
}

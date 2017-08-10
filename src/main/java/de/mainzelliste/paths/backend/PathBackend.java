package de.mainzelliste.paths.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.ws.rs.WebApplicationException;

import de.mainzelliste.paths.configuration.Paths;
import de.mainzelliste.paths.configuration.Paths.Path;

/**
 * Backend for managing path implementations.
 */
public class PathBackend {
	
	// Realisierung als Function<String, String> nur vorläufig für Prototypen!
	/** Map of path implementations */	
	private Map<String, Function<String, String>> pathImplementations;
	
	/** Initialize backend for path processing. In the web application, an
	 * instance should be retreived via {@link Controller#getPathBackend()}. */
	public PathBackend(Paths configuration) {
		
		try {
			pathImplementations = new HashMap<>();
			// Suche für jeden konfigurierten Pfad die zuständige Java-Klasse und instanziiere sie
			 for (Path path : configuration.getPaths()) {
				 Function<String, String> thisImplementation = (Function<String, String>) Class.forName(path.getImplementation()).newInstance();
				 pathImplementations.put(path.getName(), thisImplementation);
			 }
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}
	
	/**
	 * Get the names of configured paths.
	 * @return Set of path names.
	 */
	public Set<String> getPathNames() {
		return pathImplementations.keySet();
	}
	
	/** Get Implementation of a path.
	 * 
	 * @param pathName
	 *            The name of the path.
	 * @return An instance of Function, which implements the path, or null if
	 *         the path is not defined. */
	public Function<String, String> getPathImplementation(String pathName) {
		return pathImplementations.get(pathName);
	}
	
}

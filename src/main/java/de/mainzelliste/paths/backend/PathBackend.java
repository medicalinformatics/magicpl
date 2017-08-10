package de.mainzelliste.paths.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import de.mainzelliste.paths.configuration.*;

/**
 * Backend for managing path implementations.
 */
public class PathBackend {
	
	// Realisierung als Function<String, String> nur vorläufig für Prototypen!
	/** Map of path implementations */	
	private Map<String, Function<String, String>> pathImplementations;
	
	/** Initialize backend for path processing. In the web application, an
	 * instance should be retreived via {@link Controller#getPathBackend()}. */
	public PathBackend(/* TODO: Konfiguration übergeben */) {
		// Create Mockup implementations for paths "foo" and "bar"
		Paths pathConfig = new Paths();
		pathImplementations = new HashMap<>();
		
		pathImplementations.put("foo", new Function<String, String>() {

			@Override
			public String apply(String t) {
				return "Result of applying foo to input " + t;
			}
		}); 
				
		pathImplementations.put("bar", new Function<String, String>() {

			@Override
			public String apply(String t) {
				return "Result of applying bar to input " + t;
			}
		}); 
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

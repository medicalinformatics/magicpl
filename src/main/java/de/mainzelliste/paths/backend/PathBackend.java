package de.mainzelliste.paths.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import de.mainzelliste.paths.configuration.SimplePath;
import de.mainzelliste.paths.configuration.MultiPath;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.Paths;
import de.mainzelliste.paths.processor.AbstractProcessor;
import de.mainzelliste.paths.processor.MultiPathProcessor;
import de.mainzelliste.paths.processorio.AbstractProcessorIo;

/** Backend for managing path processor. */
public class PathBackend {

	// Realisierung als Function<String, String> nur vorläufig für Prototypen!
	/** Map of path processor */
	private Map<String, AbstractProcessor<?, ?>> pathImplementations;

	/**
	 * Initialize backend for path processing. In the web application, an
	 * instance should be retreived via {@link Controller#getPathBackend()}.
	 */
	public PathBackend(Paths configuration) {

		try {
			pathImplementations = new HashMap<>();
			// Suche für jeden konfigurierten Pfad die zuständige Java-Klasse
			// und instanziiere sie
			for (Path path : configuration.getPathOrMultipath()) {
				if (path instanceof SimplePath) {
					pathImplementations.put(path.getName(), instantiateProcessor(path));
				}

				if (path instanceof MultiPath) {
					MultiPath multiPath = (MultiPath) path;
					MultiPathProcessor thisImplementation = new MultiPathProcessor(path);
					for (Path thisPath : multiPath.getPath()) {
						thisImplementation.addProcessor(instantiateProcessor(thisPath));
					}
					pathImplementations.put(path.getName(), thisImplementation);
				}
			}
		} catch (Exception e) {
			throw new WebApplicationException(e);
		}
	}

	/**
	 * Get the names of configured paths.
	 * 
	 * @return Set of path names.
	 */
	public Set<String> getPathNames() {
		return pathImplementations.keySet();
	}

	/**
	 * Get Implementation of a path.
	 * 
	 * @param pathName
	 *            The name of the path.
	 * @return An instance of Function, which implements the path, or null if
	 *         the path is not defined.
	 */
	public AbstractProcessor<?, ?> getPathImplementation(String pathName) {
		return pathImplementations.get(pathName);
	}

	private AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> instantiateProcessor(Path pathDefinition)
			throws Exception {
		return (AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo>) Class
				.forName(((SimplePath) pathDefinition).getImplementation()).getConstructor(Path.class)
				.newInstance(pathDefinition);
	}
}

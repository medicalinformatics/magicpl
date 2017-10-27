package de.mainzelliste.paths.backend;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import de.mainzelliste.paths.configuration.SimplePath;
import de.mainzelliste.paths.configuration.Switch;
import de.mainzelliste.paths.evaluator.AbstractEvaluator;
import de.mainzelliste.paths.configuration.GuardedCaseType;
import de.mainzelliste.paths.configuration.MultiPath;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.Paths;
import de.mainzelliste.paths.processor.AbstractProcessor;
import de.mainzelliste.paths.processor.MultiPathProcessor;
import de.mainzelliste.paths.processor.SwitchProcessor;
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
				pathImplementations.put(path.getName(), instantiateProcessor(path));
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
		if (pathDefinition instanceof SimplePath) {
			SimplePath simplePathDefinition = (SimplePath) pathDefinition;
			if (simplePathDefinition.getImplementation() != null) {
				return (AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo>) Class
						.forName(simplePathDefinition.getImplementation()).getConstructor(Path.class)
						.newInstance(pathDefinition);
			} else if (simplePathDefinition.getSwitch() != null) {
				Switch switchDefinition = simplePathDefinition.getSwitch();
				AbstractEvaluator thisEvaluator = (AbstractEvaluator) Class.forName(switchDefinition.getEvaluator())
						.newInstance();
				SwitchProcessor switchProcessor = new SwitchProcessor(simplePathDefinition, thisEvaluator);
				for (GuardedCaseType caseDefinition : switchDefinition.getCase()) {
					Optional<Path> thisCasePath = Arrays.asList(caseDefinition.getPath(), caseDefinition.getMultipath())
							.stream().filter(s -> s != null).findFirst();
					if (thisCasePath.isPresent()) {
						switchProcessor.setCase(caseDefinition.getValue(), instantiateProcessor(thisCasePath.get()));
					} else {
						// This should never happen, as the underlying XML would
						// be invalid and an error at unmarshalling be thrown.
						throw new Error("Case " + caseDefinition.getValue() + " does not define a path.");
					}
				}
				return switchProcessor;
			} else {
				// This should never happen, as the underlying XML would be
				// invalid and an error at unmarshalling be thrown.
				throw new Error("Neither implementation nor switch defined in path " + pathDefinition.getName());
			}
		} else if (pathDefinition instanceof MultiPath) {
			MultiPath multiPath = (MultiPath) pathDefinition;
			MultiPathProcessor thisImplementation = new MultiPathProcessor(pathDefinition);
			for (Path thisPath : multiPath.getPath()) {
				thisImplementation.addProcessor(instantiateProcessor(thisPath));
			}
			return thisImplementation;
		} else {
			throw new Error("Path definition " + pathDefinition.getName() + " is neither path nor mulitpath.");
		}
	}
}

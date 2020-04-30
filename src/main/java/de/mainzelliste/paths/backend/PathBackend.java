package de.mainzelliste.paths.backend;

import de.mainzelliste.paths.exceptions.UndefinedFieldException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.WebApplicationException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.mainzelliste.paths.configuration.SimplePath;
import de.mainzelliste.paths.configuration.Switch;
import de.mainzelliste.paths.evaluator.AbstractEvaluator;
import de.mainzelliste.paths.adapters.Adapter;
import de.mainzelliste.paths.adapters.AdapterFactory;
import de.mainzelliste.paths.configuration.GuardedCaseType;
import de.mainzelliste.paths.configuration.Ioabstracttype;
import de.mainzelliste.paths.configuration.Iorecord;
import de.mainzelliste.paths.configuration.Iosingle;
import de.mainzelliste.paths.configuration.MultiPath;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.Pathconfig;
import de.mainzelliste.paths.processor.AbstractProcessor;
import de.mainzelliste.paths.processor.MultiPathProcessor;
import de.mainzelliste.paths.processor.SwitchProcessor;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

/** Backend for managing path processor. */
public class PathBackend {

	/** Map of path processor */
	private Map<String, AbstractProcessor> pathImplementations;
	private Map<String, Adapter<?>> adapters = new HashMap<>();
	private Gson gson = new Gson();

	/**
	 * Initialize backend for path processing. In the web application, an instance
	 * should be retreived via {@link Controller#getPathBackend()}.
	 * 
	 * @param configuration Configuration of the paths to be served by this
	 *                      instance.
	 */
	public PathBackend(Pathconfig configuration) {
		try {
			pathImplementations = new HashMap<>();
			// Suche für jeden konfigurierten Pfad die zuständige Java-Klasse
			// und instanziiere sie
			for (Path path : configuration.getPaths().getPathOrMultipath()) {
				pathImplementations.put(path.getName(), instantiateProcessor(path));
			}

			for (Ioabstracttype io : configuration.getIodefinitions().getIosingleOrIorecord()) {
				if (io instanceof Iorecord) {
					for (Iosingle ios : ((Iorecord) io).getIosingle()) {
						adapters.put(ios.getName(), AdapterFactory.getAdapter(ios));
					}
				}
				adapters.put(io.getName(), AdapterFactory.getAdapter(io));
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
	 * @param pathName The name of the path.
	 * @return An instance of Function, which implements the path, or null if the
	 *         path is not defined.
	 */
	public AbstractProcessor getPathImplementation(String pathName) {
		return pathImplementations.get(pathName);
	}

	/**
	 * Unmarshal input to a map of strings. Omits parsing of values to appropriate
	 * classes. This method is used to generate the appropriate format for input
	 * checking by
	 * {@link Authenticator#checkRestrictions(javax.servlet.http.HttpServletRequest, String, Map)}.
	 * 
	 * @param data Input data in JSON format.
	 * @return The parsed data.
	 */
	public Map<String, String> unmarshalToStrings(String data) {
		Type type = new TypeToken<Map<String, String>>() {
		}.getType();
		Map<String, String> stringMap = gson.fromJson(data, type);
		return stringMap;
	}

	/**
	 * Unmarshal map of strings to map of appropriate objects. Use this to fully
	 * unmarshal the map returned by {@link #unmarshalToStrings(String)}.
	 * 
	 * @param stringMap Map of strings.
	 * @return The ummarshalled map with objects of appropriate classes.
	 * @see Adapter
	 */
	public ImmutableMap<String, Object> unmarshal(Map<String, String> stringMap) {
		HashMap<String, Object> output = new HashMap<>();

		for (Map.Entry<String, String> entry : stringMap.entrySet()) {
			Adapter<?> adapter = adapters.get(entry.getKey());
			if(adapter == null) {
				throw new UndefinedFieldException(entry.getKey());
			}
			output.put(entry.getKey(), adapter.unmarshal(entry.getValue()));
		}

		return ImmutableMap.copyOf(output);
	}

	/**
	 * Directly unmarshal from JSON data. Combines the two steps of
	 * {@link #unmarshalToStrings(String)} and {@link #unmarshal(Map)}.
	 * 
	 * @param data Input data in JSON format.
	 * @return The ummarshalled map with objects of appropriate classes.
	 */
	public ImmutableMap<String, Object> unmarshal(String data) {
		Map<String, String> stringMap = unmarshalToStrings(data);
		return unmarshal(stringMap);
	}

	public String marshal(Map<String, Object> data) {
		HashMap<String, String> output = new HashMap<>();
		for (Entry<String, Object> entry : data.entrySet()) {
			try {
				Adapter adapter = AdapterFactory.getAdapter(entry.getValue().getClass());
				output.put(entry.getKey(), adapter.marshal(entry.getValue()));
			} catch (Exception e) {
				throw new WebApplicationException(e);
			}
		}
		return gson.toJson(output);
	}

	public Map<String, Object> filterPathInput(String pathName, Map<String, Object> allInput) {
		HashMap<String, Object> result = new HashMap<>(allInput);
		result.keySet().retainAll(Controller.getConfigurationBackend().getPathInputs(pathName).keySet());
		return result;
	}

	/**
	 * Instantiate a processor from a given path definition. The method calls itself
	 * recursively if needed, e.g. for sub paths of a multipath or the case-based
	 * paths inside a &lt;switch&gt; element.
	 * 
	 * @param pathDefinition Configuration of the path to instantiate.
	 * @return A processor object implementing the given path.
	 * @throws Exception If something goes wrong. //FIXME: Genauere Fehlerbehandlung
	 */
	private AbstractProcessor instantiateProcessor(Path pathDefinition) throws Exception {
		if (pathDefinition instanceof SimplePath) {
			SimplePath simplePathDefinition = (SimplePath) pathDefinition;
			if (simplePathDefinition.getImplementation() != null) {
				return (AbstractProcessor) Class.forName(simplePathDefinition.getImplementation())
						.getConstructor(Path.class).newInstance(pathDefinition);
			} else if (simplePathDefinition.getSwitch() != null) {
				Switch switchDefinition = simplePathDefinition.getSwitch();
				AbstractEvaluator thisEvaluator = (AbstractEvaluator) Class
						.forName(switchDefinition.getEvaluator().getName()).getConstructor(Switch.Evaluator.class)
						.newInstance(switchDefinition.getEvaluator());
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
				if (switchDefinition.getDefault() != null) {
					Optional<Path> defaultCasePath = Arrays
							.asList(switchDefinition.getDefault().getPath(),
									switchDefinition.getDefault().getMultipath())
							.stream().filter(s -> s != null).findFirst();
					if (defaultCasePath.isPresent()) {
						switchProcessor.setDefaultCase(instantiateProcessor(defaultCasePath.get()));
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
			for (Path thisPath : multiPath.getStep()) {
				thisImplementation.addProcessor(instantiateProcessor(thisPath));
			}
			return thisImplementation;
		} else {
			throw new Error("Path definition " + pathDefinition.getName() + " is neither path nor mulitpath.");
		}
	}
}

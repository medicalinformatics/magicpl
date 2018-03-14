package de.mainzelliste.paths.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import de.mainzelliste.paths.backend.Controller;
import de.mainzelliste.paths.configuration.ParameterMap;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.Parameters.Parameter;

/**
 * Base class for processor implementations.
 */
public abstract class AbstractProcessor implements Function<Map<String, Object>, Map<String, Object>> {

	private String pathName;
	private ParameterMap parameters;
	private Set<String> inputs;

	/**
	 * Default constructor. Initializes name (see {@link #getPathName()}) and
	 * parameter map (see {@link #getParameters()}) from the given
	 * configuration.
	 * 
	 * @param configuration
	 *            Configuration of this path.
	 */
	public AbstractProcessor(Path configuration) {
		this.pathName = configuration.getName();		
		this.parameters = new ParameterMap();
		if (configuration.getParameters() != null) {
			for (Parameter thisParam : configuration.getParameters().getParameter()) {
				parameters.put(thisParam.getName(), thisParam);
			}
		}
		inputs = Controller.getConfigurationBackend().getPathInputs(pathName).keySet();;
	}

	/**
	 * This implementation handles input filtering (i.e. only fields defined as
	 * input for the respective path are given to the processing function).
	 * Actual work is done in the subclasses' implementations of
	 * {@link #process(Map)}.
	 */
	@Override
	public final Map<String, Object> apply(Map<String, Object> t) {
		HashMap<String, Object> filteredInput = new HashMap<>(t);
		filteredInput.keySet().retainAll(this.inputs);
		return process(filteredInput);
	}

	/**
	 * Processing function. The actual work that a processor performs is
	 * implemented by overriding this method.
	 * 
	 * @param t
	 *            Input to processor.
	 * @return Output of processor.
	 */
	public abstract Map<String, Object> process(Map<String, Object> t);


	/**
	 * Get the name of the path implemented by this instance.
	 * 
	 * @return The path name as given in the configuration (see
	 *         {@link #AbstractProcessor(Path)}).
	 */
	public String getPathName() {
		return pathName;
	}

	/**
	 * Get the parameters of this implementation.
	 * 
	 * @return An unmodifiable The parameters of the implemented path as an
	 *         unmodifiable map.
	 */
	public ParameterMap getParameters() {
		return parameters;
	}
}

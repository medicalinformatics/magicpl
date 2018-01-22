package de.mainzelliste.paths.processor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import de.mainzelliste.paths.configuration.ParameterMap;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.Path.Parameters.Parameter;

/**
 * Base class for processor implementations.
 * 
 * @param <IN>
 *            Input type of this path.
 * @param <OUT>
 *            Output type of this path.
 */
//public abstract class AbstractProcessor<IN, OUT>
//		implements Function<IN, OUT> {
public abstract class AbstractProcessor implements Function<Map<String, Object>, Map<String, Object>> {

	private String pathName;
	private ParameterMap parameters;

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
	}

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
		return (ParameterMap) Collections.unmodifiableMap(parameters);
	}
}

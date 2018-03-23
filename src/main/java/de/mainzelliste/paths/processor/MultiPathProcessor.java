package de.mainzelliste.paths.processor;

import java.util.*;

import de.mainzelliste.paths.backend.Controller;
import de.mainzelliste.paths.configuration.Path;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

/**
 * Implementation of multipaths, i.e. chains of several steps executed one after
 * another.
 */
public class MultiPathProcessor extends AbstractProcessor {

	private List<AbstractProcessor> steps;

	/**
	 * Construct an instance with the given configuration (name and parameters)
	 * and steps.
	 * 
	 * @param configuration
	 *            Configuration object.
	 * @param steps
	 *            Steps in the order in which they should be applied.
	 */
	public MultiPathProcessor(Path configuration,
			AbstractProcessor... steps) {
		super(configuration);
		this.steps = Arrays.asList(steps);
		this.outputs = Controller.getConfigurationBackend().getPathOutputs(getPathName()).keySet();
	}

	/**
	 * Construct an empty instance with the given configuration (name and
	 * parameters).
	 * 
	 * @param configuration
	 *            Configuration object.
	 */
	public MultiPathProcessor(Path configuration) {
		super(configuration);
		this.steps = new LinkedList<>();
		this.outputs = Controller.getConfigurationBackend().getPathOutputs(getPathName()).keySet();
	}

	private Set<String> outputs;

	@Override
	public Map<String, Object> process(Map<String, Object> t) {
	Map<String, Object> result = new HashMap<String, Object>(t);
		for (AbstractProcessor thisProcessor : steps) {
			result.putAll(thisProcessor.apply(result));
		}

		result.keySet().retainAll(this.outputs);
		return ImmutableMap.copyOf(result);
	}

	/**
	 * Add a path (processor) to the end of the chain.
	 * 
	 * @param processor
	 *            The processor to add.
	 */
	public void addProcessor(AbstractProcessor processor) {
		steps.add(processor);
	}
}

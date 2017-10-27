package de.mainzelliste.paths.processor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.processorio.AbstractProcessorIo;

/**
 * Implementation of multipaths, i.e. chains of several paths executed one after
 * another.
 */
public class MultiPathProcessor extends AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> {

	private List<AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo>> paths;

	/**
	 * Construct an instance with the given configuratio (name and parameters)
	 * and paths.
	 * 
	 * @param configuration
	 *            Configuration object.
	 * @param paths
	 *            Paths in the order in which they should be applied.
	 */
	public MultiPathProcessor(Path configuration,
			AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo>... paths) {
		super(configuration);
		this.paths = Arrays.asList(paths);
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
		this.paths = new LinkedList<>();
	}

	@Override
	public AbstractProcessorIo apply(AbstractProcessorIo t) {
		AbstractProcessorIo result = t;
		for (AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> thisProcessor : paths) {
			result = thisProcessor.apply(result);
		}

		return result;
	}

	/**
	 * Add a path (processor) to the end of the chain.
	 * 
	 * @param processor
	 *            The processor to add.
	 */
	public void addProcessor(AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> processor) {
		paths.add(processor);
	}
}

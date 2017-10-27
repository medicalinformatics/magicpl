package de.mainzelliste.paths.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.evaluator.AbstractEvaluator;
import de.mainzelliste.paths.processorio.AbstractProcessorIo;

public class SwitchProcessor extends AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> {

	private AbstractEvaluator evaluator;
	private Map<String, AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo>> processors;
	private AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> defaultProcessor;

	public SwitchProcessor(Path configuration, AbstractEvaluator evaluator) {
		super(configuration);
		this.evaluator = evaluator;
		this.processors = new HashMap<>();
		this.defaultProcessor = null;
	}

	public void setCase(String value, AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> processor) {
		this.processors.put(value, processor);
	}

	public void setDefaultCase(AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> processor) {
		this.defaultProcessor = processor;
	}

	@Override
	public AbstractProcessorIo apply(AbstractProcessorIo t) {
		String value = evaluator.evaluate(t);
		AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> processor = processors.get(value);
		if (processor == null) {
			if (defaultProcessor != null) {
				return defaultProcessor.apply(t);
			} else {
				throw new Error("No case processor for value returned by evaluator: " + value);
			}
		}
		return processor.apply(t);
	}
}

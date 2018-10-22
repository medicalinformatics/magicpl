package de.mainzelliste.paths.processor;

import java.util.HashMap;
import java.util.Map;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.evaluator.AbstractEvaluator;

/**
 * Implementation of switch control structure in path definitions.
 */
public class SwitchProcessor extends AbstractProcessor {

    private AbstractEvaluator evaluator;
    private Map<String, AbstractProcessor> processors;
    private AbstractProcessor defaultProcessor;

    /**
     * Create an instance with the given configuration and evaluator.
     * 
     * @param configuration
     *            Path definition (for retrieving name and parameters).
     * @param evaluator
     *            The object to evaluate the switch.
     */
    public SwitchProcessor(Path configuration, AbstractEvaluator evaluator) {
		super(configuration);
		this.evaluator = evaluator;
		this.processors = new HashMap<>();
		this.defaultProcessor = null;
	}

    /**
     * Add a case.
     * 
     * @param value
     *            Value (returned by the evaluator) for which this case is used.
     * @param processor
     *            Processor to process this case.
     */
    public void setCase(String value, AbstractProcessor processor) {
        this.processors.put(value, processor);
    }

    /**
     * Set and unset default case, i.e. when the return value of the evaluator
     * does not match a value of any case.
     * 
     * @param processor
     *            The processor to use for the default case or null to disable
     *            the default case.
     */
    public void setDefaultCase(AbstractProcessor processor) {
        this.defaultProcessor = processor;
    }

    @Override
    public Map<String, Object> process(Map<String, Object> t) {
        String value = evaluator.evaluate(t);
        AbstractProcessor processor = processors.get(value);
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

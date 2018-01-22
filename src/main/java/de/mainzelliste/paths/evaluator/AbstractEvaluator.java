package de.mainzelliste.paths.evaluator;

import java.util.Map;

/**
 * Base class for an Evaluator. An Evaluator considers a given ProcessorIo (usually an input)
 * and returns an evaluation in the form of a string. The string can then used, for example,
 * in a switch-case XML.
 */
public abstract class AbstractEvaluator {
    /**
     * Checks if the given string is a valid evaluation result of this Evaluator.
     *
     * @param result Value to check for validity
     * @return true if result is a valid evaluation result
     */
    public abstract boolean isValidResult(String result);

    /**
     * Evaluates a given ProcessorIo.
     *
     * @param io ProcessorIo to evaluate.
     * @return evaluation result
     */
    public abstract String evaluate(Map<String, Object> io);
}
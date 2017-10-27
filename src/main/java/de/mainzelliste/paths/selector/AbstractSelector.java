package de.mainzelliste.paths.selector;

import de.mainzelliste.paths.processorio.AbstractProcessorIo;

/**
 * Base class for selector processor. A selector evaluates a given ProcessorIo (usually an input)
 * and returns a selection in the form of a string. The string is then used in a "case" tag within a paths XML.
 * Selectors are stateless.
 */
public abstract class AbstractSelector {
    /**
     * Checks if the given string is a valid output of this selector.
     *
     * @param result Value to check for validity.
     * @return true if result is a valid output of method
     */
    public abstract boolean isValidOutput(String result);

    /**
     * Evaluates a given ProcessorIo and makes a selection in the form of a string.
     *
     * @param io ProcessorIo to evaluate.
     * @return Selection result
     */
    public abstract String select(AbstractProcessorIo io);
}

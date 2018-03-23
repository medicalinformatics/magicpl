package de.mainzelliste.paths.evaluator;

import de.mainzelliste.paths.configuration.Switch;

import java.util.Arrays;
import java.util.Map;

/**
 * Simple example of an evaluator. Checks if a string contains only lower case
 * characters ("lower"), only upper case characters ("upper") or both ("mixed").
 */
public class UpperLowerEvaluator extends AbstractEvaluator {
	public UpperLowerEvaluator(Switch.Evaluator config) {
		super(config);
	}

	@Override
	public boolean isValidResult(String result) {
		return Arrays.asList("upper", "lower", "mixed").contains(result);
	}

	@Override
	public String evaluate(Map<String, Object> io) {
		String input = io.get(0).toString();
		if (input.equals(input.toUpperCase()))
			return "upper";
		else if (input.equals(input.toLowerCase()))
			return "lower";
		else
			return "mixed";
	}

}

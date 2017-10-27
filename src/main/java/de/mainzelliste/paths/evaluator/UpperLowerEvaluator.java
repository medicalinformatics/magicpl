package de.mainzelliste.paths.evaluator;

import java.util.Arrays;

import de.mainzelliste.paths.processorio.AbstractProcessorIo;

public class UpperLowerEvaluator extends AbstractEvaluator {

	@Override
	public boolean isValidResult(String result) {
		return Arrays.asList("upper", "lower", "mixed").contains(result);
	}

	@Override
	public String evaluate(AbstractProcessorIo io) {
		String input = io.get(0).toString();
		if (input.equals(input.toUpperCase()))
			return "upper";
		else if (input.equals(input.toLowerCase()))
			return "lower";
		else
			return "mixed";
	}

}

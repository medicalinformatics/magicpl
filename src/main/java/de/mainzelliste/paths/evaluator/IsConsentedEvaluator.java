package de.mainzelliste.paths.evaluator;

import de.mainzelliste.paths.processorio.AbstractProcessorIo;
import de.mainzelliste.paths.processorio.IDATConsentIo;

import java.util.Arrays;

/**
 * Evaluator for DKTK Consent. If DKTK patient returns "true", otherwise "false".
 */
public class IsConsentedEvaluator extends AbstractEvaluator<IDATConsentIo> {

	@Override
	public boolean isValidResult(String result) {
		return Arrays.asList("true", "false").contains(result);
	}

	@Override
	public String evaluate(IDATConsentIo io) {
		if (!io.isEmpty()) {
			return io.get(0).toString();
		}
		return "false";
	}
}

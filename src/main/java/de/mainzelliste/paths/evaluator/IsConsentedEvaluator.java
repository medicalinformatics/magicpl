package de.mainzelliste.paths.evaluator;

import de.mainzelliste.paths.configuration.Switch;

import java.util.Arrays;
import java.util.Map;

/**
 * Evaluator for DKTK Consent. If DKTK patient returns "true", otherwise
 * "false".
 */
public class IsConsentedEvaluator extends AbstractEvaluator {
    public IsConsentedEvaluator(Switch.Evaluator config) {
        super(config);
    }

    @Override
    public boolean isValidResult(String result) {
        return Arrays.asList("true", "false").contains(result);
    }

    @Override
    public String evaluate(Map<String, Object> io) {
        if (!io.isEmpty()) {
            return io.get("Einwilligungsstatus").toString();
        }
        return "false";
    }
}

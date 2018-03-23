package de.mainzelliste.paths.evaluator;

import de.mainzelliste.paths.configuration.Switch;

import java.util.Map;

public class EqualEvaluator extends AbstractEvaluator {
    public EqualEvaluator(Switch.Evaluator config) {
        super(config);
    }

    @Override
    public boolean isValidResult(String result) {
        return true;
    }

    @Override
    public String evaluate(Map<String, Object> io) {
        String key = getConfig().getParameters().getParameter().get(0).getValue();
        return io.get(key).toString();
    }
}

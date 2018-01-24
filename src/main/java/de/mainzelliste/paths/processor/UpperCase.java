package de.mainzelliste.paths.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.mainzelliste.paths.configuration.Path;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

/** Example implementation of a path. Converts input to upper case. */
public class UpperCase extends AbstractProcessor {

	public UpperCase(Path configuration) {
		super(configuration);
	}

	@Override
	public Map<String, Object> apply(Map<String, Object> input) {
		HashMap<String, Object> output = new HashMap<>();
		for (Entry<String, Object> entry : input.entrySet()) {
			Object thisValue = entry.getValue();
			if (!String.class.isAssignableFrom(thisValue.getClass())) {
				throw new IllegalArgumentException(
						"Input " + thisValue + " is of class " + thisValue.getClass() + ", but String is required.");
			}
			output.put(entry.getKey(), ((String) thisValue).toUpperCase());
		}
		return ImmutableMap.copyOf(output);

	}
}

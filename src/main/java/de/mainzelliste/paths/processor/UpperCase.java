package de.mainzelliste.paths.processor;

import java.util.function.Function;

/**
 * Example implementation of a path. Converts input to upper case.
 */
public class UpperCase implements Function<String, String> {

	@Override
	public String apply(String t) {
		return t == null ? null : t.toUpperCase();
	}

}

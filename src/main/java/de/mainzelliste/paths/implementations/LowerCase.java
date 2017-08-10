package de.mainzelliste.paths.implementations;

import java.util.function.Function;

/**
 * Example implementation of a path. Converts input to lower case.
 */
public class LowerCase implements Function<String, String> {

	@Override
	public String apply(String t) {
		return t == null ? null : t.toLowerCase(); 
	}

}

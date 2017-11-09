package de.mainzelliste.paths.processor;

import java.util.Iterator;
import java.util.LinkedList;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.processorio.StringIo;

/** Example implementation of a path. Converts input to lower case. */
public class LowerCase extends AbstractProcessor<StringIo, StringIo> {

	public LowerCase(Path configuration) {
		super(configuration);
	}

	@Override
	public StringIo apply(StringIo input) {
		LinkedList<String> output = new LinkedList<>();
		Iterator valueIterator = input.iterator();
		while (valueIterator.hasNext()) {
			Object thisValue = valueIterator.next();
			if (thisValue == null) {
				output.add(null);
			} else {
				if (!String.class.isAssignableFrom(thisValue.getClass())) {
					throw new IllegalArgumentException("Input " + thisValue + " is of class " + thisValue.getClass()
							+ ", but String is required.");
				}
				output.add(((String) thisValue).toLowerCase());
			}
		}
		
		return new StringIo(output.toArray(new String[output.size()]));

	}
}

package de.mainzelliste.paths.implementations;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.processorio.AbstractProcessorIo;

/** Example implementation of a path. Converts input to lower case. */
public class LowerCase extends AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> {

	public LowerCase(Path configuration) {
		super(configuration);
	}

	@Override
	public AbstractProcessorIo apply(AbstractProcessorIo input) {
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
				output.add((String) thisValue);
			}
		}

		return new AbstractProcessorIo(output.toArray()) {

			@Override
			public List<Class<?>> getContentTypes() {
				return new ScalarContentTypeList(String.class, output.size());
			}
		};
	}

}

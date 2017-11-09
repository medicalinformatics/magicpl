package de.mainzelliste.paths.processor;

import java.util.LinkedList;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.processorio.AbstractProcessorIo;
import de.mainzelliste.paths.processorio.StringIo;

/** Dummy implementation of Mainzelliste Client */
public class MainzellisteClient extends AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> {

	public MainzellisteClient(Path configuration) {
		super(configuration);
	}

	@Override
	public AbstractProcessorIo apply(AbstractProcessorIo t) {
		LinkedList<String> result = new LinkedList<>();
		for (int i = 0; i < t.size(); i++) {
			Object thisItem = t.get(i);
			if (!(thisItem instanceof String)) {
				throw new IllegalArgumentException("Illegal type for argument " + i + ": " + thisItem.getClass());
			}
			String controlNumber = "tkt(" + thisItem + ")";
			result.add(controlNumber);
		}
		return new StringIo(result.toArray(new String[result.size()]));
	}
}

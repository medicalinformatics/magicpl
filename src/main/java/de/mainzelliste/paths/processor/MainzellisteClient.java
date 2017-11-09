package de.mainzelliste.paths.processor;

import java.util.LinkedList;
import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.processorio.AbstractProcessorIo;
import de.mainzelliste.paths.processorio.StringIo;
import de.mainzelliste.paths.processorio.IDATConsentIo;
import de.mainzelliste.paths.processorio.IDIo;

/** Implementation of Mainzelliste Client */
public class MainzellisteClient extends AbstractProcessor<IDATConsentIo, IDIo> {

	public MainzellisteClient(Path configuration) {
		super(configuration);
	}

	@Override
	public IDIo apply(IDATConsentIo t) {
		LinkedList<String> result = new LinkedList<>();
		for (int i = 0; i < t.size(); i++) {
			Object thisItem = t.get(i);
			if (!(thisItem instanceof String)) {
				throw new IllegalArgumentException("Illegal type for argument " + i + ": " + thisItem.getClass());
			}
		}
		return new IDIo();
	}
}

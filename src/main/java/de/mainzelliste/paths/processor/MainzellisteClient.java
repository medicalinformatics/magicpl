package de.mainzelliste.paths.processor;

import java.util.LinkedList;
import java.util.List;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.processorio.AbstractProcessorIo;
import de.mainzelliste.paths.processorio.ControllNumberIO;
import de.mainzelliste.paths.processorio.StringIo;
import de.mainzelliste.paths.util.ScalarContentTypeList;
import de.pseudonymisierung.controlnumbers.ControlNumber;

/** Dummy implementation of Mainzelliste Client */
public class MainzellisteClient extends AbstractProcessor<AbstractProcessorIo, AbstractProcessorIo> {

	public MainzellisteClient(Path configuration) {
		super(configuration);
	}

	@Override
	public AbstractProcessorIo apply(AbstractProcessorIo input) {
		String[] result = new String[input.size()];

		if(!(input instanceof ControllNumberIO)){
			throw new IllegalArgumentException("Input " + input + " is of class " + input.getClass()
			                                   + ", but controllNumberIO is required.");
		}

		for (int i = 0; i < input.size(); i++) {
			ControlNumber thisItem = (ControlNumber)input.get(i);

			String ticket = "tkt(" + thisItem + ")";
			result[i] = ticket;
		}
		return new StringIo(result);
	}
}

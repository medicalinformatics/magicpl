package de.mainzelliste.paths.processorio;

import java.util.Arrays;
import java.util.List;
import de.pseudonymisierung.mainzelliste.client.ID;

public class IDIo extends AbstractProcessorIo {

	public IDIo(ID ... content) {
		super(content);
	}

	@Override
	public List<Class<?>> getContentTypes() {
		return Arrays.asList(ID.class);
	}

	@Override
	public void unmarshal(String data) {
		this.set(0, data);
	}

	@Override
	public String marshal() {
		if (this.size() > 0)
			return "";
		else
			return this.get(0).toString();
	}
}

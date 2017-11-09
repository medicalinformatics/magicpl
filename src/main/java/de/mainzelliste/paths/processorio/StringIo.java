package de.mainzelliste.paths.processorio;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.mainzelliste.paths.util.ScalarContentTypeList;

public class StringIo extends AbstractProcessorIo {

	public StringIo(String ... content) {
		super(content);
	}
	
	@Override
	public List<Class<?>> getContentTypes() {
		return Arrays.asList(String.class);
	}

	// TODO: Erweitern für Listen von Strings? Im Moment werden nur ganze
	// Strings verarbeitet
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

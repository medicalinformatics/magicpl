package de.mainzelliste.paths.processorio;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import de.mainzelliste.paths.util.ScalarContentTypeList;

public class IDATConsentIo extends AbstractProcessorIo {

	public IDATConsentIo(Map<String, String> ... content) {
		super(content);
	}

	@Override
	public List<Class<?>> getContentTypes() {
		return Arrays.asList(String.class);
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

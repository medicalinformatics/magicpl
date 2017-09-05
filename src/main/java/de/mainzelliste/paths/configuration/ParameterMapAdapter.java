package de.mainzelliste.paths.configuration;

import java.util.LinkedList;
import java.util.Map.Entry;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.mainzelliste.paths.configuration.Paths.Path.Parameters;
import de.mainzelliste.paths.configuration.Paths.Path.Parameters.Parameter;

public class ParameterMapAdapter extends XmlAdapter<Parameters, ParameterMap>{

	@Override
	public ParameterMap unmarshal(Parameters v) throws Exception {
		ParameterMap result = new ParameterMap();
		for (Parameter p : v.parameter) {
			result.put(p.name, p);
		}
		return result;
	}

	@Override
	public Parameters marshal(ParameterMap v) throws Exception {
		Parameters result = new Parameters();
		result.parameter = new LinkedList<>();
		for (Entry<String, Parameter> entry : v.entrySet()) {
			Parameter p = new Parameter();			
			p.setName(entry.getKey());
			p.setValue(entry.getValue().value);
			result.parameter.add(p);
		}
		return result;
	}
	
	
	
}

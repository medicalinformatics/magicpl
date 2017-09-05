package de.mainzelliste.paths.configuration;

import java.util.HashMap;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.mainzelliste.paths.configuration.Paths.Path.Parameters.Parameter;


@XmlJavaTypeAdapter(ParameterMapAdapter.class)
public class ParameterMap extends HashMap<String, Parameter>{
	
}

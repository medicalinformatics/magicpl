package de.mainzelliste.paths.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for convenient access to path configurations.
 */
public class ConfigurationBackend {

	private Pathconfig configuration;
	
	private HashMap<String, Iosingle> singleInputDefinitions = new HashMap<>();
	private HashMap<String, Iorecord> recordInputDefinitions = new HashMap<>();
	private HashMap<String, HashMap<String, Iosingle>> singleInputsByPath = new HashMap<>();
	
	public ConfigurationBackend(Pathconfig configuration) {
		this.configuration = configuration;
		for (Ioabstracttype ioDef : configuration.iodefinitions.iosingleOrIorecord) {
			if (ioDef instanceof Iosingle) {
				singleInputDefinitions.put(ioDef.getName(), (Iosingle) ioDef);
			} else if (ioDef instanceof Iorecord) {
				recordInputDefinitions.put(ioDef.getName(), (Iorecord) ioDef);
			}
		}
		
		for (Path thisPath : this.configuration.getPaths().getPathOrMultipath()) {
			HashMap<String, Iosingle> thisPathSingleInputs = new HashMap<>();
			for (Ioabstractref thisIoRef : thisPath.getInput().getIosingleOrIorecord()) {
				if (thisIoRef instanceof Iosingleref) {
					thisPathSingleInputs.put(thisIoRef.getRef(), singleInputDefinitions.get(thisIoRef.getRef()));
				}
			}
			singleInputsByPath.put(thisPath.getName(), thisPathSingleInputs);
		}
	}
	
	public Map<String, Iosingle> getPathInputs(String pathName) {
		return singleInputsByPath.get(pathName);
		
	}
}

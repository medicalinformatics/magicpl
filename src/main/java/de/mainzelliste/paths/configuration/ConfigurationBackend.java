package de.mainzelliste.paths.configuration;

import de.samply.common.config.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for convenient access to path and proxy configurations.
 */
public class ConfigurationBackend {

	private Pathconfig configuration;
	private Configuration proxy;
	
	private HashMap<String, Iosingle> singleInputDefinitions = new HashMap<>();
	private HashMap<String, Iorecord> recordInputDefinitions = new HashMap<>();
	private HashMap<String, HashMap<String, Iosingle>> singleInputsByPath = new HashMap<>();

	public Configuration getProxy() {
		return proxy;
	}

	public ConfigurationBackend(Pathconfig configuration, Configuration proxy) {
		this.configuration = configuration;
		this.proxy = proxy;
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
				} else if (thisIoRef instanceof Iorecordref) {
					for (Iosingle thisIoSingle : recordInputDefinitions.get(thisIoRef.getRef()).getIosingle()) {
						thisPathSingleInputs.put(thisIoSingle.getName(), thisIoSingle);
					}
				}
			}
			singleInputsByPath.put(thisPath.getName(), thisPathSingleInputs);
		}
	}
	
	public Map<String, Iosingle> getPathInputs(String pathName) {
		return singleInputsByPath.get(pathName);
		
	}
}

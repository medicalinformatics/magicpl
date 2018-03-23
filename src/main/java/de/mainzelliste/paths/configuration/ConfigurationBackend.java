package de.mainzelliste.paths.configuration;

import de.samply.common.config.Configuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class for convenient access to path and proxy configurations.
 */
public class ConfigurationBackend {

	private Pathconfig configuration;
	private Configuration proxy;
	
	private HashMap<String, Iosingle> singleInputDefinitions = new HashMap<>();
	private HashMap<String, Iorecord> recordInputDefinitions = new HashMap<>();
	private HashMap<String, Map<String, Iosingle>> singleInputsByPath = new HashMap<>();
	private HashMap<String, Map<String, Iosingle>> singleOutputsByPath = new HashMap<>();

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
			initPathInputs(thisPath);
			initPathOutputs(thisPath);
		}
	}
	
	public Map<String, Iosingle> getPathInputs(String pathName) {
		return singleInputsByPath.get(pathName);

	}

	public Map<String, Iosingle> getPathOutputs(String pathName) {
		return singleOutputsByPath.get(pathName);
	}

	private void initPathInputs(Path pathConfig) {
		singleInputsByPath.put(pathConfig.getName(), getPathInputs(pathConfig));
		if (pathConfig instanceof SimplePath) {
			Switch switchConfig = ((SimplePath) pathConfig).getSwitch();
			if (switchConfig != null) {
				LinkedList<DefaultCaseType> allCases = new LinkedList<>(switchConfig.getCase());

				if(switchConfig.getDefault() != null) {
					allCases.add(switchConfig.getDefault());
				}

				for (DefaultCaseType thisCase : allCases) {
					if (thisCase.getPath() != null)
						initPathInputs(thisCase.getPath());
					if (thisCase.getMultipath() != null)
						initPathInputs(thisCase.getMultipath());
				}
			}
		} else if (pathConfig instanceof MultiPath) {
			MultiPath multiPathConfig = (MultiPath) pathConfig;
			for (SimplePath thisStep : multiPathConfig.getStep()) {
				initPathInputs(thisStep);
			}
		}
	}

	private void initPathOutputs(Path pathConfig) {
		singleOutputsByPath.put(pathConfig.getName(), getPathOutputs(pathConfig));
		if (pathConfig instanceof SimplePath) {
			Switch switchConfig = ((SimplePath) pathConfig).getSwitch();
			if (switchConfig != null) {
				LinkedList<DefaultCaseType> allCases = new LinkedList<>(switchConfig.getCase());

				if (switchConfig.getDefault() != null) {
					allCases.add(switchConfig.getDefault());
				}

				for (DefaultCaseType thisCase : allCases) {
					if (thisCase.getPath() != null)
						initPathInputs(thisCase.getPath());
					if (thisCase.getMultipath() != null)
						initPathOutputs(thisCase.getMultipath());
				}
			}
		} else if (pathConfig instanceof MultiPath) {
			MultiPath multiPathConfig = (MultiPath) pathConfig;
			for (SimplePath thisStep : multiPathConfig.getStep()) {
				initPathOutputs(thisStep);
			}
		}
	}
	
	private Map<String, Iosingle> getPathInputs(Path pathConfig) {
		HashMap<String, Iosingle> thisPathSingleInputs = new HashMap<>();
		for (Ioabstractref thisIoRef : pathConfig.getInput().getIosingleOrIorecord()) {
			if (thisIoRef instanceof Iosingleref) {
				thisPathSingleInputs.put(thisIoRef.getRef(), singleInputDefinitions.get(thisIoRef.getRef()));
			} else if (thisIoRef instanceof Iorecordref) {
				String ioRecRef = thisIoRef.getRef();
				Iorecord ioRec = recordInputDefinitions.get(ioRecRef);
				List<Iosingle> ioSingles = ioRec.getIosingle();
				for (Iosingle thisIoSingle : ioSingles) {
					thisPathSingleInputs.put(thisIoSingle.getName(), thisIoSingle);
				}
			}
		}
		return thisPathSingleInputs;
	}

	private Map<String, Iosingle> getPathOutputs(Path pathConfig) {
		HashMap<String, Iosingle> thisPathSingleOutputs = new HashMap<>();
		for (Ioabstractref thisIoRef : pathConfig.getOutput().getIosingleOrIorecord()) {
			if (thisIoRef instanceof Iosingleref) {
				thisPathSingleOutputs.put(thisIoRef.getRef(), singleInputDefinitions.get(thisIoRef.getRef()));
			} else if (thisIoRef instanceof Iorecordref) {
				String ioRecRef = thisIoRef.getRef();
				Iorecord ioRec = recordInputDefinitions.get(ioRecRef);
				List<Iosingle> ioSingles = ioRec.getIosingle();
				for (Iosingle thisIoSingle : ioSingles) {
					thisPathSingleOutputs.put(thisIoSingle.getName(), thisIoSingle);
				}
			}
		}
		return thisPathSingleOutputs;
	}

	public Pathconfig getConfiguration() {
		return this.configuration;
	}
}

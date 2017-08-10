package de.mainzelliste.paths.backend;

import java.util.function.Function;

public enum Controller {
	
	instance;
	
	private PathBackend pathBackend;
	
	private Controller() {
		// Initialize Application
		this.pathBackend = new PathBackend();
	}

	public PathBackend getPathBackend() {
		return pathBackend;
	}
}

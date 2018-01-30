package de.mainzelliste.paths.processor;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.SimplePath;

import java.lang.reflect.Constructor;

public class ProcessorFactory {

    public static AbstractProcessor getProcessor(SimplePath config) throws Exception {
        String processorName = config.getImplementation();

        if (!processorName.contains(".")) {
            processorName = "de.mainzelliste.paths.processor." + processorName;
        }

        Class<?> clazz = Class.forName(processorName);
        Constructor<?> constructor = clazz.getConstructor(Path.class);
        Object object = constructor.newInstance(config);

        if (! (object instanceof AbstractProcessor)) {
            throw new IllegalArgumentException("The class" + config.getImplementation() + " is not a processor");
        }

        return (AbstractProcessor) object;
    }
}

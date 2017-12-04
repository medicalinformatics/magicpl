package de.mainzelliste.paths.processor;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.SimplePath;

import java.beans.Statement;
import java.lang.reflect.Constructor;
import java.util.List;

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

        List<Path.Parameters.Parameter> params = config.getParameters().getParameter();

        for (Path.Parameters.Parameter param : params) {
            String method = "set"+ param.getName().substring(0,1).toUpperCase() + param.getName().substring(1);
            Object[] value = {param.getValue()};
            Statement stmnt = new Statement(object, method, value);
            stmnt.execute();
        }

        return (AbstractProcessor) object;
    }
}

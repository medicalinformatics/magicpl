package de.mainzelliste.paths.adapters;

import de.mainzelliste.paths.configuration.Iorecord;
import de.mainzelliste.paths.configuration.Iosingle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AdapterFactory {

    public static Adapter getAdapter(Iosingle config) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String adapterName = config.getAdapter();

        if (!adapterName.contains(".")) {
            adapterName = "de.mainzelliste.paths.adapters." + adapterName;
        }

        Class<?> clazz = Class.forName(adapterName);

        Object object = clazz.newInstance();

        if(object instanceof Adapter){
            return (Adapter) object;
        }

        throw new IllegalArgumentException("The class" + config.getAdapter() + " is not a adapter");
    }

    public static Adapter getAdapter(Iorecord config) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        String adapterName = config.getAdapter();

        if(! adapterName.contains(".")){
            adapterName = "de.mainzelliste.paths.adapters." + adapterName;
        }

        Class<?> clazz = Class.forName(adapterName);

        Constructor<?> constructor = clazz.getConstructor(Iorecord.class);

        Object object = constructor.newInstance(config);

        if (object instanceof Adapter) {
            return (Adapter) object;
        }

        throw new IllegalArgumentException("The class" + config.getAdapter() + " is not a adapter");
    }

}

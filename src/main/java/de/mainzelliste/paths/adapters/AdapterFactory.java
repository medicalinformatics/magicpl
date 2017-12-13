package de.mainzelliste.paths.adapters;

import de.mainzelliste.paths.configuration.Iorecord;
import de.mainzelliste.paths.configuration.Iosingle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AdapterFactory {

    public static Adapter getAdapter(Iosingle config) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        // e.g. StringAdapter (data type, per default String + Adapter)
        String adapterName = config.getType() + "Adapter";

        if (!adapterName.contains(".")) {
            adapterName = Adapter.class.getPackage().getName() + "." + adapterName;
        }

        System.out.print("adapterName: " + adapterName + "\n");

        Class<?> clazz = Class.forName(adapterName);

        Object object = clazz.newInstance();

        if(object instanceof Adapter){
            return (Adapter) object;
        }

        throw new IllegalArgumentException("The class" + config.getType() + "Adapter is not a adapter");
    }

    public static Adapter getAdapter(Iorecord config) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        // Iorecord supports only default Adapter: ImmutableMapAdapter
        Object object = new ImmutableMapAdapter(config);
        return (Adapter) object;
    }
}

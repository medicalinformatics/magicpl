package de.mainzelliste.paths.adapters;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.mainzelliste.paths.configuration.Iorecord;
import de.mainzelliste.paths.configuration.Iosingle;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ImmutableMapAdapter implements Adapter<ImmutableMap<String, Object>> {

    private HashMap<String, Adapter> adapters;

    @Override
    public ImmutableMap<String, Object> unmarshal(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> stringMap = gson.fromJson(data, type);
        Map<String, Object> contentMap = new HashMap<>(stringMap.size());

        for (Map.Entry<String, String> entry : stringMap.entrySet()) {
            Adapter adapter = adapters.get(entry.getKey());
            contentMap.put(entry.getKey(), adapter.unmarshal(entry.getValue()));
        }

        return ImmutableMap.copyOf(contentMap);
    }

    @Override
    public String marshal(ImmutableMap<String, Object> object) {
        Map<String, String> stringMap = new HashMap<>(object.size());

        for (Map.Entry<String, Object> entry : object.entrySet()) {
            Adapter adapter = adapters.get(entry.getKey());
            stringMap.put(entry.getKey(), adapter.marshal(entry.getValue()));
        }

        Gson gson = new Gson();
        return gson.toJson(stringMap);
    }

    public ImmutableMapAdapter(Iorecord config) throws IllegalAccessException, InstantiationException, ClassNotFoundException {

        adapters = new HashMap<>();

        for (Iosingle iosingle : config.getIosingle()) {
            adapters.put(iosingle.getName(), AdapterFactory.getAdapter(iosingle));
        }


    }
}

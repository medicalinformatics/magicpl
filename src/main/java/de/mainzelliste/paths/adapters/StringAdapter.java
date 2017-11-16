package de.mainzelliste.paths.adapters;

public class StringAdapter implements Adapter<String>{
    @Override
    public String unmarshal(String data) {
        return data;
    }

    @Override
    public String marshal(String object) {
        return object;
    }
}

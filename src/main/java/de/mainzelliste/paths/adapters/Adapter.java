package de.mainzelliste.paths.adapters;

public interface Adapter<T> {

    T unmarshal(String data);

    String marshal(T object);
}

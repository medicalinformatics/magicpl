package de.mainzelliste.paths.adapters;

public class BooleanAdapter implements Adapter<Boolean> {

	@Override
	public Boolean unmarshal(String data) {
		// TODO Auto-generated method stub
		return Boolean.parseBoolean(data);
	}

	@Override
	public String marshal(Boolean object) {
		// TODO Auto-generated method stub
		return object.toString();
	}

}

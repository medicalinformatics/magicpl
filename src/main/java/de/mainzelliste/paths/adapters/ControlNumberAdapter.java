package de.mainzelliste.paths.adapters;

import de.pseudonymisierung.controlnumbers.ControlNumber;

public class ControlNumberAdapter implements Adapter<ControlNumber> {
    @Override
    public ControlNumber unmarshal(String data) {
        return ControlNumber.fromBitString(data);
    }

    @Override
    public String marshal(ControlNumber object) {
        return object.toBitString();
    }
}

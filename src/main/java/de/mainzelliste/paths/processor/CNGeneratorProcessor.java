package de.mainzelliste.paths.processor;

import de.mainzelliste.paths.configuration.Path;
import de.pseudonymisierung.controlnumbers.ControlNumber;
import de.pseudonymisierung.controlnumbers.ControlNumberGenerator;
import de.pseudonymisierung.controlnumbers.EncryptedControlNumberGenerator;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class CNGeneratorProcessor extends AbstractProcessor {
    private ControlNumberGenerator controlNumberGenerator;

    public CNGeneratorProcessor(Path configuration) {
        super(configuration);
        final String passphrase = "passphrase";
        if(configuration.getParameters().getParameter().size() != 1){
            throw new IllegalArgumentException("This processor requires exactly one parameter called " + passphrase
                                               + ". Please check your path config.");
        }

        Path.Parameters.Parameter parameter = configuration.getParameters().getParameter().get(0);

        if(! passphrase.equals(parameter.getName())){
            throw new IllegalArgumentException("Paratername is wrong. It should be " + passphrase
                                               + " Please check your path config.");
        }

        this.controlNumberGenerator = EncryptedControlNumberGenerator.builder(parameter.getValue()).build();
    }

    @Override
    public Map<String, Object> apply(Map<String, Object> input) {
        HashMap<String, ControlNumber> output = new HashMap<>();


        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if (entry.getValue() == null) {
                output.put(entry.getKey(), null);
            } else {
                output.put(entry.getKey(), controlNumberGenerator.apply(entry.getValue().toString()));
            }
        }

        return ImmutableMap.copyOf(output);
    }
}

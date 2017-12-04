package de.mainzelliste.paths.processor;

import de.mainzelliste.paths.adapters.Adapter;
import de.mainzelliste.paths.configuration.Path;
import de.pseudonymisierung.controlnumbers.ControlNumber;
import de.pseudonymisierung.controlnumbers.ControlNumberGenerator;
import de.pseudonymisierung.controlnumbers.EncryptedControlNumberGenerator;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class CNGeneratorProcessor extends AbstractProcessor<ImmutableMap<String, Object>, ImmutableMap<String, ControlNumber>> {
    private ControlNumberGenerator controlNumberGenerator;

    public CNGeneratorProcessor(Path configuration) {
        super(configuration);

    }

    public void setPassphrase(String passphrase){
        this.controlNumberGenerator = EncryptedControlNumberGenerator.builder(passphrase).build();
    }

    @Override
    public ImmutableMap<String, ControlNumber> apply(ImmutableMap<String, Object> input) {
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

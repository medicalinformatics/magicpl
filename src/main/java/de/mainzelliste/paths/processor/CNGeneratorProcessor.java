package de.mainzelliste.paths.processor;

import de.mainzelliste.paths.configuration.Path;
import de.mainzelliste.paths.configuration.Parameters;
import de.pseudonymisierung.controlnumbers.ControlNumber;
import de.pseudonymisierung.controlnumbers.ControlNumberGenerator;
import de.pseudonymisierung.controlnumbers.EncryptedControlNumberGenerator;
import jersey.repackaged.com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public class CNGeneratorProcessor extends AbstractProcessor {
    private EncryptedControlNumberGenerator controlNumberGenerator;

    public CNGeneratorProcessor(Path configuration) {
        super(configuration);
        final String passphrase = "passphrase";
        if (configuration.getParameters().getParameter().size() != 1) {
            throw new IllegalArgumentException("This processor requires exactly one parameter called " + passphrase
                    + ". Please check your path config.");
        }

        Parameters.Parameter parameter = configuration.getParameters().getParameter().get(0);

        if (!passphrase.equals(parameter.getName())) {
            throw new IllegalArgumentException("Paratername is wrong. It should be " + passphrase
                    + " Please check your path config.");
        }

        this.controlNumberGenerator = EncryptedControlNumberGenerator.builder(parameter.getValue()).build();
    }

    @Override
    public Map<String, Object> process(Map<String, Object> input) {
      HashMap<String, Object> output = new HashMap<>();


        for (Map.Entry<String, Object> entry : input.entrySet()) {
            if (entry.getValue() == null) {
                output.put(entry.getKey(), null);
            } else {
                if (entry.getKey().equals("locallyUniqueId")) {
                    output.put("locallyUniqueIdEnc", controlNumberGenerator.encrypt(entry.getValue().toString()));
                }else if (entry.getKey().equals("IDType")) {
                    output.put(entry.getKey(), entry.getValue());
                } else
                {
                    output.put(entry.getKey() + "CN", controlNumberGenerator.apply(entry.getValue().toString()));
                }
            }
        }

        return ImmutableMap.copyOf(output);
    }
}

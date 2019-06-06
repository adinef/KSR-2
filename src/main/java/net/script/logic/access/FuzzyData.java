package net.script.logic.access;

import net.script.config.main.ApplicationVariables;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.ConfigAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FuzzyData {

    private final ConfigAccessor<Qualifier> qualifierConfigAccessor;
    private final ConfigAccessor<Quantifier> quantifierConfigAccessor;

    @Autowired
    public FuzzyData(ConfigAccessor<Qualifier> qualifierConfigAccessor, ConfigAccessor<Quantifier> quantifierConfigAccessor) {
        this.qualifierConfigAccessor = qualifierConfigAccessor;
        this.quantifierConfigAccessor = quantifierConfigAccessor;
    }

    public synchronized List<Quantifier> quantifiers() throws Exception {
        if (ApplicationVariables.determineRuntimeConfig()) {
            return quantifierConfigAccessor.read(false);
        }
        return quantifierConfigAccessor.read();
    }

    public synchronized List<Qualifier> qualifiers() throws Exception {
        if (ApplicationVariables.determineRuntimeConfig()) {
            return qualifierConfigAccessor.read(false);
        }
        return qualifierConfigAccessor.read();
    }

    public synchronized void realoadData() throws Exception {
        qualifierConfigAccessor.read(false);
        quantifierConfigAccessor.read(false);
    }

    public synchronized void saveQualifiers() throws Exception {
        qualifierConfigAccessor.saveCachedData();;
    }

    public synchronized void saveQuantifiers() throws Exception {
        quantifierConfigAccessor.saveCachedData();;
    }
}

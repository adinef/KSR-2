package net.script.logic.access;

import net.script.config.main.ApplicationVariables;
import net.script.data.annotations.Function;
import net.script.logic.fuzzy.functions.FunctionSetting;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.ConfigAccessor;
import net.script.logic.settings.FunctionsSettings;
import net.script.logic.summarizer.Summarizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class FuzzyData {

    private final ConfigAccessor<Qualifier> qualifierConfigAccessor;
    private final ConfigAccessor<Quantifier> quantifierConfigAccessor;
    private final ConfigAccessor<Summarizer> summarizerConfigAccessor;

    @Autowired
    public FuzzyData(ConfigAccessor<Qualifier> qualifierConfigAccessor,
                     ConfigAccessor<Quantifier> quantifierConfigAccessor,
                     ConfigAccessor<Summarizer> summarizerConfigAccessor) {
        this.qualifierConfigAccessor = qualifierConfigAccessor;
        this.quantifierConfigAccessor = quantifierConfigAccessor;
        this.summarizerConfigAccessor = summarizerConfigAccessor;
    }

    public <T extends QFunction> Optional<QFunction> createFunctionForClass(Class<T> qFunctionClass, Double... params) {
        FunctionSetting functionSetting = FunctionsSettings
                    .SingleFunctionSetting
                    .builder()
                    .name(this.functionNameFromClass(qFunctionClass))
                    .coefficients(this.extractCoefficients(params))
                    .build();
        try {
            return Optional.of(QFunctionFactory.getFunction(functionSetting));
        } catch (Exception e) {
            return Optional.empty();
        }
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

    public synchronized List<Summarizer> summarizers() throws Exception {
        if (ApplicationVariables.determineRuntimeConfig()) {
            return summarizerConfigAccessor.read(false);
        }
        return summarizerConfigAccessor.read();
    }

    public synchronized void realoadData() throws Exception {
        qualifierConfigAccessor.read(false);
        quantifierConfigAccessor.read(false);
        summarizerConfigAccessor.read(false);
    }

    public synchronized void saveQualifiers() throws Exception {
        qualifierConfigAccessor.saveCachedData();
    }

    public synchronized void saveQuantifiers() throws Exception {
        quantifierConfigAccessor.saveCachedData();
    }

    public synchronized void saveSummarizers() throws Exception {
        summarizerConfigAccessor.saveCachedData();
    }

    private <T extends QFunction> String functionNameFromClass(Class<T> tClass) {
        return tClass.getAnnotation(Function.class).value();
    }

    private Map<String, Double> extractCoefficients(Double[] params) {
        Map<String, Double> coefficients = new HashMap<>();
        String coeff = "a";
        if (params != null) {
            for (Double param : params) {
                coefficients.put(coeff, param);
                coeff = String.valueOf( (char) (coeff.charAt(0) + 1) );
            }
        }
        return coefficients;
    }
}

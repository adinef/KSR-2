package net.script.logic.fuzzy.functions;

import java.util.Map;

public interface FunctionSetting {
    String getName();
    Map<String, Double> getCoefficients();
}

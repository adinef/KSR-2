package net.script.logic.settings.functions;

import java.util.Map;

public interface FunctionSetting {
    String getName();
    Map<String, Double> getCoefficients();
}

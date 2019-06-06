package net.script.logic.fuzzy.functions.factory;

import lombok.NonNull;
import net.script.logic.fuzzy.functions.*;

import java.util.Map;

public class QFunctionFactory {
    public static QFunction getFunction(FunctionSetting functionSetting) {
        String name = functionSetting.getName();
        Map<String, Double> coefficients = functionSetting.getCoefficients();
        if ("trapezoid".equalsIgnoreCase(name)) {
            @NonNull Double a = coefficients.getOrDefault("a", null);
            @NonNull Double b = coefficients.getOrDefault("b", null);
            @NonNull Double c = coefficients.getOrDefault("c", null);
            @NonNull Double d = coefficients.getOrDefault("d", null);
            return new TrapezoidFunction(a, b, c ,d);
        } else if ("triangular".equalsIgnoreCase(name)) {
            @NonNull Double a = coefficients.getOrDefault("a", null);
            @NonNull Double b = coefficients.getOrDefault("b", null);
            return new TriangleFunction(a, b);
        } else if ("rectangular".equalsIgnoreCase(name)) {
            @NonNull Double a = coefficients.getOrDefault("a", null);
            @NonNull Double b = coefficients.getOrDefault("b", null);
            return new RectangularFunction(a, b);
        } else {
            throw new RuntimeException("Could not load QFunction. Not found.");
        }
    }
}

package net.script.logic.fuzzy.functions;

public interface QFunction {
    double calculate(double x);
    double calculateIntegral();
    double distance();
}

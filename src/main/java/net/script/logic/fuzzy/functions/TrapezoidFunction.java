package net.script.logic.fuzzy.functions;

import lombok.AllArgsConstructor;
import net.script.data.annotations.Function;
import net.script.data.annotations.Coefficient;

@AllArgsConstructor
@Function("trapezoid")
public class TrapezoidFunction implements QFunction {

    @Coefficient("a")
    private final double a;

    @Coefficient("b")
    private final double b;

    @Coefficient("c")
    private final double c;

    @Coefficient("d")
    private final double d;

    @Override
    public double calculate(double x) {
        if (x > a && x < b) {
            return ((1 / (b - a)) * (x - a));
        } if (x > c && x < d) {
            return (-(1/(d-c)) * (x - d));
        } else if (x >= b && x <= c) {
            return 1;
        }
        return 0;
    }

    @Override
    public double distance() {
        return d - b;
    }

    @Override
    public double square() {
        return (a - b) * (d - c);
    }

    @Override
    public double calculateIntegral() {
        double value = c - b;
        value += 0.5 * (b - a);
        value += 0.5 * (d-c);
        return value;
    }

    @Override
    public String toString() {
        return String.format("Funkcja trapezoidalna, współczynniki: %f, %f, %f, %f", a, b, c, d);
    }
}

package net.script.logic.fuzzy.functions;

import lombok.AllArgsConstructor;
import net.script.data.annotations.Function;
import net.script.data.annotations.Coefficient;

import static java.lang.Math.abs;

@AllArgsConstructor
@Function("triangular")
public class TriangularFunction implements QFunction {

    @Coefficient("a")
    private final double a;

    @Coefficient("b")
    private final double b;

    @Coefficient("c")
    private final double c;

    @Override
    public double calculate(double x) {
        if (x == b) {
            return 1;
        } else if ( x > a && x <= b) {
            return (1.0/(b-a)) * (x - a);
        } else if (x > b && x < c) {
            return (-1.0/(c-b))*(x-c);
        } else
        return 0;
    }

    @Override
    public double calculateIntegral() {
        return 0.5 * (c - a);
    }

    @Override
    public double distance() {
        return c-a;
    }

    @Override
    public String toString() {
        return String.format("Funkcja triangularna, współczynniki: %s, %s, %s", a, b, c);
    }
}

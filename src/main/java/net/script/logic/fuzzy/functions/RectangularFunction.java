package net.script.logic.fuzzy.functions;

import lombok.AllArgsConstructor;
import net.script.data.annotations.Coefficient;
import net.script.data.annotations.Function;

import static java.lang.Math.abs;

@AllArgsConstructor
@Function("rectangular")
public class RectangularFunction implements QFunction {

    @Coefficient("a")
    private final double a;

    @Coefficient("b")
    private final double b;

    @Override
    public double calculate(double x) {
        if (x <= b || x >= a) {
            return 1;
        }
        return 0;
    }

    @Override
    public double distance() {
        return b-a;
    }

    @Override
    public double square() {
        return 1;
    }

    @Override
    public String toString() {
        return String.format("Funkcja prostokatna, współczynniki: %f, %f", a, b);
    }
}

package net.script.logic.fuzzy.functions;

import lombok.AllArgsConstructor;
import net.script.data.annotations.Function;
import net.script.data.annotations.Coefficient;

import static java.lang.Math.abs;

@AllArgsConstructor
@Function("triangular")
public class TriangleFunction implements QFunction {

    @Coefficient("a")
    private final double num;

    @Coefficient("b")
    private final double dis;

    @Override
    public double calculate(double x) {
        if (x > num - dis && x < num + dis) {
            if (x < num) {
                return ((1/dis) * (x - (num - dis)));
            } else {
                return (-(1/dis) * (x - (num + dis)));
            }
        }
        return 0;
    }

    @Override
    public double distance() {
        return num;
    }

    @Override
    public double square() {
        return num - dis;
    }

    @Override
    public String toString() {
        return String.format("Funkcja triangularna, współczynniki: %f, %f", num, dis);
    }
}

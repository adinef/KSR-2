package net.script.logic.fuzzy.functions;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class TrapezoidFunction implements QFunction {

    private final double a;
    private final double b;
    private final double c;
    private final double d;

    @Override
    public double calculate(double x) {
        if (x >= a && x <= c) {
            return 1;
        } else if (x > b && x < a) {
            return (x - b) / (a - b);
        } else if (x > c && x < d) {
            return (d - x) / (d - c);
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
}

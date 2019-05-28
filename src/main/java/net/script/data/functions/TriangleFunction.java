package net.script.data.functions;

import lombok.AllArgsConstructor;
import lombok.ToString;

import static java.lang.Math.*;

@AllArgsConstructor
@ToString
public class TriangleFunction implements QFunction {

    private final double num;
    private final double dis;

    @Override
    public double calculate(double x) {
        if (x > num - dis && x < num + dis) {
            if (x < num) {
                return abs( abs(x) - abs(num) - dis ) / dis;
            } else {
                return abs( abs(num) + dis - abs(x) ) / dis;
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
}

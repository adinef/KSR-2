package net.script.logic.quantifier;

import lombok.*;
import net.script.data.Named;
import net.script.data.annotations.Column;
import net.script.logic.fuzzy.functions.QFunction;

@Data
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quantifier implements Named {

    @Column("Nazwa")
    private String name;

    @Column("Funkcja")
    private QFunction function;

    public double calculate(double x) {
        return getFunction().calculate(x);
    }
}

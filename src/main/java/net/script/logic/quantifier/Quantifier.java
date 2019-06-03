package net.script.logic.quantifier;

import lombok.*;
import net.script.data.annotations.Comment;
import net.script.data.annotations.enums.Author;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.fuzzy.linguistic.Range;

@Comment(
        value = "Klasa reprezentująca kwantyfikator",
        madeBy = Author.AdrianFijalkowski
)
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
public class Quantifier extends LinguisticVariable {

    public Quantifier(String name, String member, QFunction function, Range range) {
        super(name, member, function, range);
    }

    public double calculate(double x) {
        return getFunction().calculate(x);
    }

    public double cardinalNumber() {
        return getFunction().distance();
    }
}

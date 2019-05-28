package net.script.logic.quantifier;

import lombok.*;
import net.script.data.annotations.Comment;
import net.script.data.annotations.enums.Author;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;

@EqualsAndHashCode(callSuper = true)
@Comment(
        value = "Klasa reprezentująca kwantyfikator",
        madeBy = Author.AdrianFijalkowski
)
@Data
@Builder
public class Quantifier extends LinguisticVariable {
    public double calculate(double x) {
        return getFunction().calculate(x);
    }

    public double cardinalNumber() {
        return getFunction().distance();
    }
}

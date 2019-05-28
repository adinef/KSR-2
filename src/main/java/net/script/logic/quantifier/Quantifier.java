package net.script.logic.quantifier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.annotations.Comment;
import net.script.data.annotations.enums.Author;
import net.script.logic.fuzzy.functions.QFunction;

@Comment(
        value = "Klasa reprezentująca kwantyfikator",
        madeBy = Author.AdrianFijalkowski
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quantifier {
    private QFunction function;
    private String name;

    public double calculate(double x) {
        return function.calculate(x);
    }

    public double cardinalNumber() {
        return function.distance();
    }
}

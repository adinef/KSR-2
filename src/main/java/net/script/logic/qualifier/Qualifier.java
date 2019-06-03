package net.script.logic.qualifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.fuzzy.linguistic.Range;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Qualifier extends LinguisticVariable {
    public Qualifier(String name, String member, QFunction function, Range range) {
        super(name, member, function, range);
    }
}

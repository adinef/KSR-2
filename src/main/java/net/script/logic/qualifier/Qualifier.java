package net.script.logic.qualifier;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Qualifier extends LinguisticVariable {
    public Qualifier(String name, String member, QFunction function) {
        super(name, member, function);
    }
}

package net.script.logic.summarizer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.fuzzy.linguistic.Range;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class Summarizer extends LinguisticVariable {
    public Summarizer(String name, String member, QFunction function, Range range) {
        super(name, member, function, range);
    }
}

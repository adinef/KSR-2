package net.script.logic.fuzzy.linguistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.logic.fuzzy.functions.QFunction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinguisticVariable {
    private String name;
    private String memberFieldName;
    private QFunction function;
}

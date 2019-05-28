package net.script.logic.qualificator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.logic.fuzzy.functions.QFunction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Qualificator {
    private String name;
    private String memberFieldName;
    private QFunction function;
}

package net.script.logic.fuzzy.linguistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.Named;
import net.script.data.annotations.Column;
import net.script.logic.fuzzy.functions.QFunction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinguisticVariable implements Named {

    @Column("Nazwa")
    private String name;

    @Column("Uczestnik")
    private String memberFieldName;

    @Column("Funkcja")
    private QFunction function;

    @Column("Przestrzeń rozważań")
    private Range lvRange;
}

package net.script.logic.summary.qualities;

import net.script.logic.quantifier.Quantifier;
import net.script.logic.summary.SummarizationState;

import java.util.List;
import java.util.stream.Collectors;

public class DegreeOfQuantifierCardinalityT7 {
    public static double calculateDegreeOfQuantifierCardinality(SummarizationState StateSummary, String name){
        Quantifier quantifier = StateSummary.getQuantfiers().stream().filter(s -> s.getName().trim().equals(name)).collect(Collectors.toList()).get(0);
        return 1 - quantifier.getFunction().calculateIntegral();
    }
}

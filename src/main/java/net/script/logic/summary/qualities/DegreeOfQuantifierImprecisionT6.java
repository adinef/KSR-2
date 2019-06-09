package net.script.logic.summary.qualities;

import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summary.SummarizationState;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DegreeOfQuantifierImprecisionT6 implements QualityMeasure {
    @Override
    public double calculateQualityValue(List<?> Data, SummarizationState StateSummaryTuple) {
        return 0;
    }

    public static double calculateDegreeOfQuantifierImprecision(SummarizationState summarizationState, String name) {
        Quantifier quantifier = summarizationState.getQuantfiers().stream().filter(s -> s.getName().trim().equals(name)).collect(Collectors.toList()).get(0);
        if(quantifier.getFunction().getClass().getName().equals("TriangleFunction")) {
            return 1.0 - quantifier.getFunction().distance()/2;
        } else {
            return 1.0 - quantifier.getFunction().distance();
        }
    }
}
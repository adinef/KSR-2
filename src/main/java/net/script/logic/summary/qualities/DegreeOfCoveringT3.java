package net.script.logic.summary.qualities;

import net.script.data.Tuple;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.summary.SummarizationState;
import net.script.view.Summary;

import java.util.List;

public class DegreeOfCoveringT3 implements QualityMeasure {
    @Override
    public double calculateQualityValue(List<?> Data, SummarizationState StateSummaryTuple) {
        return 0;
    }

    public static double calculateDegreeOfCovering(List<?> Data, SummarizationState StateSummaryTuple) {
        double m;
        if (StateSummaryTuple.getQualifiers() != null && StateSummaryTuple.getQualifiers().size() > 0) {
            m = calculateSumOfTi2ndType(Data,StateSummaryTuple);
        }
        else m = Data.size();
        return FuzzySet.sumWithCardinality(StateSummaryTuple.getFinalFuzzySet(),1) / m;
    }

    private static double calculateSumOfTi2ndType(List<?> Data, SummarizationState summarizationState) {
        FuzzySet qualSet = FuzzySet.with(Data).from(summarizationState.getQualifiers().get(0));
        for (Qualifier qualifier : summarizationState.getQualifiers()) {
            FuzzySet.intersect(qualSet, FuzzySet.with(Data).from(qualifier));
        }
        return FuzzySet.sumWithCardinality(qualSet,1);
    }
}

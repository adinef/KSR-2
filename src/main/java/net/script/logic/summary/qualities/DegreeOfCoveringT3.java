package net.script.logic.summary.qualities;

import net.script.data.Tuple;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.summary.SummarizationState;
import net.script.view.Summary;

import java.util.List;

public class DegreeOfCoveringT3  {


    public static double calculateDegreeOfCovering(List<?> Data, SummarizationState StateSummaryTuple, boolean isAndQualifier) {
        double m;
        if (StateSummaryTuple.getQualifiers() != null && StateSummaryTuple.getQualifiers().size() > 0) {
            m = calculateSumOfTi2ndType(Data,StateSummaryTuple, isAndQualifier);
        }
        else m = Data.size();
        return FuzzySet.sumWithCardinality(StateSummaryTuple.getFinalFuzzySet(),1) / m;
    }

    private static double calculateSumOfTi2ndType(List<?> Data, SummarizationState summarizationState, boolean isAndQualifier) {
        FuzzySet qualSet = FuzzySet.with(Data).from(summarizationState.getQualifiers().get(0));
        if(isAndQualifier){
            for (Qualifier qualifier : summarizationState.getQualifiers()) {
                FuzzySet.intersect(qualSet, FuzzySet.with(Data).from(qualifier));
            }
        } else {
            for (Qualifier qualifier : summarizationState.getQualifiers()) {
                FuzzySet.sum(qualSet, FuzzySet.with(Data).from(qualifier));
            }
        }
        return FuzzySet.sumWithCardinality(qualSet,1);
    }
}

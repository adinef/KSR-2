package net.script.logic.summary.qualities;

import net.script.logic.summary.SummarizationState;

import java.util.List;

public class LengthOfSummaryT5 implements QualityMeasure {
    @Override
    public double calculateQualityValue(List<?> Data, SummarizationState StateSummaryTuple) {
        return 0;
    }

    public static double calculateLengthOfSummaryT5(SummarizationState summarizationState) {
        return 2 * Math.pow(0.5, (double) summarizationState.getSummarizers().size());
    }


}

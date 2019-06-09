package net.script.logic.summary.qualities;

import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.SummarizationState;

import java.util.List;

public class DegreeOfApproppriatenessT4 implements QualityMeasure {

    @Override
    public double calculateQualityValue(List<?> Data, SummarizationState StateSummary) {
        return 0;
    }

    public static double calculateDegreeOfAppropriateness(List<?> Data, SummarizationState StateSummary, double T3) {
        double val = 1;
        for(Summarizer summarizer : StateSummary.getSummarizers()){
            FuzzySet fuzzySet = FuzzySet.with(Data).from(summarizer);
            val *= (fuzzySet.support().size() * 1.0 / Data.size());
        }
        return Math.abs(val-T3);
    }
}

package net.script.logic.summary.qualities;

import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.SummarizationState;

import java.util.List;

public class DegreeOfSummarizerCardinalityT8 {
    public static double calculateDegreeOfSummarizerCardinality(List<?> data, SummarizationState StateSummary){
        double val = 1;
        for (Summarizer s : StateSummary.getSummarizers()) {
            val *= FuzzySet.sumWithCardinality(FuzzySet.with(data).from(s),1)/data.size();
        }
        return 1.0 - Math.pow(val, 1.0 / StateSummary.getSummarizers().size());
    }
}

package net.script.logic.summary.qualities;

import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.summary.SummarizationState;

import java.util.List;

public class DegreeOfQualifierCardinalityT10 {
    public static double calculateDegreeOfQualifierCardinality(List<?> data, SummarizationState StateSummary) {
        if(StateSummary.getQualifiers().size()<=0)
            return -1;
        double val = 1;
        for (Qualifier q : StateSummary.getQualifiers()) {
            val *= FuzzySet.sumWithCardinality(FuzzySet.with(data).from(q), 1) / data.size();
        }
        return 1.0 - Math.pow(val, 1.0 / StateSummary.getQualifiers().size());
    }
}

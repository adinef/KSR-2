package net.script.logic.summary.qualities;

import net.script.logic.qualifier.Qualifier;
import net.script.logic.summary.SummarizationState;

import java.util.List;

public class DegreeOfQualifierImprecisionT9 {

    public static double calculateDegreeOfQualifierImprecision(SummarizationState StateSummary) {
        if(StateSummary.getQualifiers().size()<=0)
            return -1;
        double value = 1;
        for (int i = 0; i < StateSummary.getQualifiers().size(); i++) {
            value *= calculateIn(StateSummary.getQualifiers().get(i));
        }
        return 1 - Math.pow(value, 1.0 / StateSummary.getQualifiers().size());
    }

    private static double calculateIn(Qualifier q) {
        return q.getFunction().distance() / (q.getLvRange().getEnd() - q.getLvRange().getBegin());
    }
}

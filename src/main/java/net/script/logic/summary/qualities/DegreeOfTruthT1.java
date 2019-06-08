package net.script.logic.summary.qualities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.Tuple;
import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.SummarizationState;
import net.script.view.Summary;

import javax.swing.plaf.nimbus.State;
import java.util.List;

@Data
public class DegreeOfTruthT1 implements QualityMeasure {

    private double value;

    public DegreeOfTruthT1(List<?> Data, Tuple<Summary, SummarizationState> StateSummaryTuple) {
        calculateQualityValue(Data,StateSummaryTuple.getSecond());
    }

    @Override
    public double calculateQualityValue(List<?> Data,SummarizationState StateSummaryTuple) {
        value = calculateR(Data, StateSummaryTuple);
        return value;//return calculateR(Data,StateSummaryTuple);
    }

    public static double calculateR(List<?> Data, SummarizationState StateSummary) {
        double r;
        if (StateSummary.getQualifiers() != null && StateSummary.getQualifiers().size() > 0) {
            FuzzySet qualSet = FuzzySet.with(Data).from(StateSummary.getQualifiers().get(0));
            for (Qualifier qualifier : StateSummary.getQualifiers()) {
                FuzzySet.intersect(qualSet, FuzzySet.with(Data).from(qualifier));
            }
            r = FuzzySet.sumWithCardinality(StateSummary.getFinalFuzzySet(), 1) / FuzzySet.sumWithCardinality(qualSet, 1);
        } else {
            r = FuzzySet.sumWithCardinality(StateSummary.getFinalFuzzySet(), 1) / Data.size();
        }
        //System.out.println(r);
        return r;
    }


}

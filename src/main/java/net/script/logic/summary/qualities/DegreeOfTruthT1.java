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
public class DegreeOfTruthT1 {

    private double value;


    public static double calculateR(List<?> Data, SummarizationState StateSummary, boolean isAndQualifier) {
        double r;
        if (StateSummary.getQualifiers() != null && StateSummary.getQualifiers().size() > 0) {
            FuzzySet qualSet = FuzzySet.with(Data).from(StateSummary.getQualifiers().get(0));
            if(isAndQualifier && StateSummary.getQualifiers().size()>=2){
                for (int i = 1; i < StateSummary.getQualifiers().size(); i++) {
                    qualSet = FuzzySet.intersect(qualSet, FuzzySet.with(Data).from(StateSummary.getQualifiers().get(i)));
                    System.out.println("weszło tutaj");
                }
            } else if (StateSummary.getQualifiers().size()>=2){
                for (int i = 1; i < StateSummary.getQualifiers().size(); i++) {
                    qualSet = FuzzySet.sum(qualSet, FuzzySet.with(Data).from(StateSummary.getQualifiers().get(i)));
                }
            }
            r = FuzzySet.sumWithCardinality(StateSummary.getFinalFuzzySet(), 1) / FuzzySet.sumWithCardinality(qualSet, 1);
        } else {
            r = FuzzySet.sumWithCardinality(StateSummary.getFinalFuzzySet(), 1) / Data.size();
        }
        //System.out.println(r);
        return r;
    }


}

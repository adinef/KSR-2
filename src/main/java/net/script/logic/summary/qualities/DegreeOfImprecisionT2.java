package net.script.logic.summary.qualities;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.script.data.Tuple;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.SummarizationState;
import net.script.view.Summary;

import java.util.List;

@Data
@NoArgsConstructor
public class DegreeOfImprecisionT2  {


    public static double calculateDegreeOfImprecision(SummarizationState StateSummary) {
        double value = 1;
        for (int i = 0; i < StateSummary.getSummarizers().size(); i++) {
            value *= calculateIn(StateSummary.getSummarizers().get(i));
        }
        value = Math.pow(value, 1.0 / StateSummary.getSummarizers().size());
        value = 1 - value;
        return value;
    }

    private static double calculateIn(Summarizer s) {
        return s.getFunction().distance() / (s.getLvRange().getEnd()-s.getLvRange().getBegin());
    }


    /*@Override
    public double calculateQualityValue(List<?> dataList, FuzzySet<?> finalSet, List<Quantifier> quantifiers, List<Qualifier> qualifiers, List<Summarizer> summarizers) {
        double value=1;
        for(int i = 0; i < summarizers.size(); i++) {
            value *=in(summarizers.get(i),dataList);
        }
        value = Math.pow(value,1.0/summarizers.size());
        return value;
    }

    private double in(Summarizer s, List<?> dataList) {
        FuzzySet summarizerSet = FuzzySet.with(dataList).from(s);
        double in = summarizerSet.support().size() * 1.0/summarizerSet.size();
        return in;
    }*/
}

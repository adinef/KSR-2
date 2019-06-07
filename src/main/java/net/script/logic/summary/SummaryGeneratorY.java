package net.script.logic.summary;

import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.view.MainController;
import net.script.view.Summary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SummaryGeneratorY { // Q obiektów jest/ma S
    public static Summary createSummaryFirstType(Iterable<DCResMeasurement> dcResMeasurements, List<Quantifier> quantifiers, Summarizer summarizer) {
        Iterator<DCResMeasurement> source = dcResMeasurements.iterator();
        List<DCResMeasurement> DCList = new ArrayList<>();
        source.forEachRemaining(DCList::add);
        FuzzySet summarizersSet = FuzzySet.with(DCList).from(summarizer);
        double finalSizeNormalized = 0;
        if (summarizersSet.size() != 0) {
            finalSizeNormalized = summarizersSet.support().size() * 1.0 / summarizersSet.size();
        }
        System.out.println("NAN");
        System.out.println(finalSizeNormalized);
        double max = -1.0;
        String name = "";
        for (Quantifier q : quantifiers) {
            if (q.calculate(finalSizeNormalized) > max) {
                max = q.calculate(finalSizeNormalized);
                name = q.getName();
            }
        }
        Summary s = new Summary();
        String podsumowanie = name + " budynków ma ";
        podsumowanie += summarizer.getName();
        s.setContent(podsumowanie);
        s.setDegreeOfTruth(max);
        return s;
    }
}

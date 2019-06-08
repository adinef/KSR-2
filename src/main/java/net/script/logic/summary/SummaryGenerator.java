package net.script.logic.summary;

import net.script.data.Tuple;
import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.view.Summary;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SummaryGenerator {

    private final List<Summary> summaries = new ArrayList<>();
    private final String entityName;
    private final String standardContext;

    public SummaryGenerator(String entityName) {
        this.entityName = entityName;
        this.standardContext = "%s " + entityName + " ma/jest ";
    }

    public List<Summary> createSummary(Iterable<DCResMeasurement> dcResMeasurements,
                                       List<Quantifier> quantifiers,
                                       List<Qualifier> qualifiers,
                                       List<Summarizer> summarizers) {
        summaries.clear();
        Iterator<?> source = dcResMeasurements.iterator();
        List<Object> dataList = new ArrayList<>();
        source.forEachRemaining(dataList::add);
        //TYPU PIERWSZEGO
        //pojedynczy sumaryzator
        for (Summarizer s : summarizers) {
            summaries.add(this.createSummaryFirstType(dataList, quantifiers, s));
        }
        //dwa złączone
        if (summarizers.size() >= 2) {
            List<Summarizer> tempSumList = new ArrayList<>();
            for (int i = 0; i < summarizers.size(); i++) {
                for (int j = summarizers.size() - 1; j > i; j--) {
                    tempSumList.clear();
                    if (!summarizers.get(i).getMemberFieldName().equals(summarizers.get(j).getMemberFieldName())) {
                        tempSumList.add(summarizers.get(i));
                        tempSumList.add(summarizers.get(j));
                        summaries.add(this.createSummaryFirstTypeMultipleSummarizers(dataList, quantifiers, tempSumList));
                    }
                }
            }
            //wszystkie jeśli dotyczą innych kategorii
            List<Summarizer> uniqueCategorySummarizers = summarizers.stream().filter(distinctByKey(Summarizer::getMemberFieldName)).collect(Collectors.toList());
            if (summarizers.size() == uniqueCategorySummarizers.size() && summarizers.size() > 2) {
                summaries.add(this.createSummaryFirstTypeMultipleSummarizers(dataList, quantifiers, summarizers));
            }
        }
        //TYPU DRUGIEGO PERMUTACJE
        //pojedynczy sumaryzator i kwalifikator
        if (qualifiers.size() > 0) {
            for (Summarizer s : summarizers) {
                for (Qualifier q : qualifiers) {
                    summaries.add(this.createSummaryTypeTwo(dataList, quantifiers, Collections.singletonList(q), Collections.singletonList(s)));
                }
            }
            //pojedynczy kwalifikator i 2 sumaryzatory
            if (summarizers.size() >= 2) {
                List<Summarizer> tempSumList = new ArrayList<>();
                for (Qualifier q : qualifiers) {
                    for (int i = 0; i < summarizers.size(); i++) {
                        for (int j = summarizers.size() - 1; j > i; j--) {
                            tempSumList.clear();
                            if (!summarizers.get(i).getMemberFieldName().equals(summarizers.get(j).getMemberFieldName())) {
                                tempSumList.add(summarizers.get(i));
                                tempSumList.add(summarizers.get(j));
                                summaries.add(this.createSummaryTypeTwo(dataList, quantifiers, Collections.singletonList(q), tempSumList));
                            }
                        }
                    }
                }
            }
            if (qualifiers.size() >= 2) {
                List<Qualifier> tempQualList = new ArrayList<>();
                for (Summarizer s : summarizers) {
                    for (int i = 0; i < qualifiers.size(); i++) {
                        for (int j = qualifiers.size() - 1; j > i; j--) {
                            tempQualList.clear();
                            if (!qualifiers.get(i).getMemberFieldName().equals(qualifiers.get(j).getMemberFieldName())) {
                                tempQualList.add(qualifiers.get(i));
                                tempQualList.add(qualifiers.get(j));
                                summaries.add(this.createSummaryTypeTwo(dataList, quantifiers, tempQualList, Collections.singletonList(s)));
                            }
                        }
                    }
                }
            }
            if(qualifiers.size()>=2 && quantifiers.size() >= 2) {
                List<Summarizer> tempSumList = new ArrayList<>();
                List<Qualifier> tempQualList = new ArrayList<>();
                for (int i = 0; i < qualifiers.size(); i++) {
                    for (int j = qualifiers.size() - 1; j > i; j--) {
                        tempQualList.clear();
                        if (!qualifiers.get(i).getMemberFieldName().equals(qualifiers.get(j).getMemberFieldName())) {
                            tempQualList.add(qualifiers.get(i));
                            tempQualList.add(qualifiers.get(j));
                        }
                        for (int k = 0; k < summarizers.size(); k++) {
                            for (int l = summarizers.size() - 1; l > k; l--) {
                                tempSumList.clear();
                                if (!summarizers.get(k).getMemberFieldName().equals(summarizers.get(l).getMemberFieldName())) {
                                    tempSumList.add(summarizers.get(k));
                                    tempSumList.add(summarizers.get(l));
                                    summaries.add(this.createSummaryTypeTwo(dataList, quantifiers, tempQualList, tempSumList));
                                }
                            }
                        }
                    }
                }
            }
            //wszystkie jeśli dotyczą innych kategorii
            List<Summarizer> uniqueCategorySummarizers = summarizers.stream().filter(distinctByKey(Summarizer::getMemberFieldName)).collect(Collectors.toList());
            List<Qualifier> uniqueCategoryQualifiers = qualifiers.stream().filter(distinctByKey(Qualifier::getMemberFieldName)).collect(Collectors.toList());
            if (summarizers.size() == uniqueCategorySummarizers.size() && summarizers.size() > 2 &&
            qualifiers.size() == uniqueCategoryQualifiers.size() && qualifiers.size() > 2) {
                summaries.add(this.createSummaryTypeTwo(dataList, quantifiers, qualifiers, summarizers));
            }
        }
        return summaries;
    }

    private Summary createSummaryFirstType(List<?> dataList,
                                           List<Quantifier> quantifiers,
                                           Summarizer summarizer) {
        FuzzySet summarizersSet = FuzzySet.with(dataList).from(summarizer);

        double finalSizeNormalized = calculateNormalized(summarizersSet);
        System.out.println(finalSizeNormalized);

        Tuple<String, Double> nameMaxTuple = this.extractNameAndMax(quantifiers, finalSizeNormalized);
        double max = nameMaxTuple.getSecond();
        String name = nameMaxTuple.getFirst();

        String podsumowanie = String.format(this.standardContext, name);
        podsumowanie += summarizer.getName();

        return new Summary(podsumowanie, max);
    }

    private Summary createSummaryFirstTypeMultipleSummarizers(List<?> dataList,
                                                              List<Quantifier> quantifiers,
                                                              List<Summarizer> summarizers) {
        List<FuzzySet> summarySets = this.extractFromLinguisticVariables(dataList, summarizers);

        FuzzySet summarizersSet = waterfallIntersect(summarySets);

        double finalSizeNormalized = calculateNormalized(summarizersSet);
        System.out.println(finalSizeNormalized);

        Tuple<String, Double> nameMaxTuple = this.extractNameAndMax(quantifiers, finalSizeNormalized);
        double max = nameMaxTuple.getSecond();
        String name = nameMaxTuple.getFirst();

        String summaryContent = this.buildSummaryContext(String.format(this.standardContext, name), summarizers);

        return new Summary(summaryContent, max);
    }

    private Summary createSummaryTypeTwo(List<?> dataList,
                                         List<Quantifier> quantifiers,
                                         List<Qualifier> qualifiers,
                                         List<Summarizer> summarizers) {
        List<FuzzySet> calculatedSets = this.extractFromLinguisticVariables(dataList, qualifiers);

        FuzzySet intersectSet = waterfallIntersect(calculatedSets);

        calculatedSets.clear();
        for (Summarizer summarizer : summarizers) {
            calculatedSets.add(FuzzySet.with(dataList).from(summarizer));
        }
        for (FuzzySet calculatedSet : calculatedSets) {
            intersectSet = FuzzySet.intersect(intersectSet, calculatedSet);
        }

        double finalSizeNormalized = calculateNormalized(intersectSet);
        System.out.println(finalSizeNormalized);

        Tuple<String, Double> nameMaxTuple = this.extractNameAndMax(quantifiers, finalSizeNormalized);
        double max = nameMaxTuple.getSecond();
        String name = nameMaxTuple.getFirst();

        String summaryContent =
                this.buildSummaryContext(String.format("%s %s, które mają/są ", name, this.entityName), qualifiers);
         summaryContent += this.buildSummaryContext(" ma/jest ", summarizers);

        return new Summary(summaryContent, max);
    }


    //HELPER METHODS

    private double calculateNormalized(FuzzySet intersectedSet) {
        double finalSizeNormalized = 0;
        if (intersectedSet.size() != 0) {
            finalSizeNormalized = intersectedSet.support().size() * 1.0 / intersectedSet.size();
        }
        return finalSizeNormalized;
    }

    private String buildSummaryContext(String beginning, List<? extends LinguisticVariable> lvs) {
        for (LinguisticVariable summarizer : lvs) {
            beginning += (summarizer.getName());
            if (lvs.indexOf(summarizer) <= lvs.size() - 2)
                beginning += (" i ");
        }
        return beginning;
    }

    private Tuple<String, Double> extractNameAndMax(List<? extends Quantifier> lvs, double finalSizeNormalized) {
        double max = -1.0;
        String name = "";
        for (Quantifier q : lvs) {
            if (q.calculate(finalSizeNormalized) > max) {
                max = q.calculate(finalSizeNormalized);
                name = q.getName();
            }
        }
        return new Tuple<>(name, max);
    }

    private FuzzySet waterfallIntersect(List<FuzzySet> fuzzySets){
        FuzzySet firstSet = fuzzySets.get(0);
        if (firstSet.size() >= 2) {
            for (int i = 1; i < fuzzySets.size(); i++) {
                firstSet = FuzzySet.intersect(firstSet, fuzzySets.get(i));
            }
        }
        return firstSet;
    }

    private <T> List<FuzzySet> extractFromLinguisticVariables(List<T> dataList, List<? extends LinguisticVariable> lvs) {
        List<FuzzySet> calculatedSets = new ArrayList<>();
        for (LinguisticVariable lv : lvs) {
            calculatedSets.add(FuzzySet.with(dataList).from(lv));
        }
        return calculatedSets;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}



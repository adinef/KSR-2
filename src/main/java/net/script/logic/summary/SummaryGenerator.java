package net.script.logic.summary;

import net.script.data.Tuple;
import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.qualities.*;
import net.script.view.Summary;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SummaryGenerator {

    private final List<Tuple<Summary, SummarizationState>> summaries = new ArrayList<>();
    private final String entityName;
    private final String standardContext;

    public SummaryGenerator(String entityName) {
        this.entityName = entityName;
        this.standardContext = "%s " + entityName + " ma/jest ";
    }

    public List<Tuple<Summary, SummarizationState>> createSummary(Iterable<DCResMeasurement> dcResMeasurements,
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
            if (qualifiers.size() >= 2 && quantifiers.size() >= 2) {
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

    private Tuple<Summary, SummarizationState> createSummaryFirstType(List<?> dataList,
                                                                      List<Quantifier> quantifiers,
                                                                      Summarizer summarizer) {
        FuzzySet summarizersSet = FuzzySet.with(dataList).from(summarizer);

        /*double finalSizeNormalized = calculateNormalized(summarizersSet);
        System.out.println(finalSizeNormalized);*/

        SummarizationState summarizationState =
                new SummarizationState(
                        new ArrayList<>(),
                        quantifiers,
                        Collections.singletonList(summarizer),
                        summarizersSet);

        Tuple<String, Double> nameMaxTuple = this.extractNameAndMax(quantifiers, DegreeOfTruthT1.calculateR(dataList, summarizationState));
        double max = nameMaxTuple.getSecond();
        String name = nameMaxTuple.getFirst();

        String summaryContent = String.format(this.standardContext, name);
        summaryContent += summarizer.getName();

        Map<String, Double> qualityMeasures = calculateQualityMeasures(dataList, summarizationState, name);

        return new Tuple<>(
                new Summary(summaryContent, max,
                        qualityMeasures.get("T2"),
                        qualityMeasures.get("T3"),
                        qualityMeasures.get("T4"),
                        qualityMeasures.get("T5"),
                        qualityMeasures.get("T6"),
                        qualityMeasures.get("T7"),
                        qualityMeasures.get("T8"),
                        qualityMeasures.get("T9"),
                        qualityMeasures.get("T10"),
                        qualityMeasures.get("T11")),
                new SummarizationState(
                        new ArrayList<>(),
                        null,
                        Collections.singletonList(summarizer),
                        summarizersSet
                )
        );
    }

    private Tuple<Summary, SummarizationState> createSummaryFirstTypeMultipleSummarizers(List<?> dataList,
                                                                                         List<Quantifier> quantifiers,
                                                                                         List<Summarizer> summarizers) {
        List<FuzzySet> summarySets = this.extractFromLinguisticVariables(dataList, summarizers);

        FuzzySet summarizersSet = waterfallIntersect(summarySets);

        /*double finalSizeNormalized = calculateNormalized(summarizersSet);
        System.out.println(finalSizeNormalized);*/

        SummarizationState summarizationState =
                new SummarizationState(
                        new ArrayList<>(),
                        quantifiers,
                        summarizers,
                        summarizersSet);

        Tuple<String, Double> nameMaxTuple = this.extractNameAndMax(quantifiers, DegreeOfTruthT1.calculateR(dataList, summarizationState));
        double max = nameMaxTuple.getSecond();
        String name = nameMaxTuple.getFirst();

        String summaryContent = this.buildSummaryContext(String.format(this.standardContext, name), summarizers);

        Map<String, Double> qualityMeasures = calculateQualityMeasures(dataList, summarizationState, name);

        return new Tuple<>(
                new Summary(summaryContent, max,
                        qualityMeasures.get("T2"),
                        qualityMeasures.get("T3"),
                        qualityMeasures.get("T4"),
                        qualityMeasures.get("T5"),
                        qualityMeasures.get("T6"),
                        qualityMeasures.get("T7"),
                        qualityMeasures.get("T8"),
                        qualityMeasures.get("T9"),
                        qualityMeasures.get("T10"),
                        qualityMeasures.get("T11")),
                new SummarizationState(
                        new ArrayList<>(),
                        quantifiers,
                        summarizers,
                        summarizersSet
                )
        );
    }

    private Tuple<Summary, SummarizationState> createSummaryTypeTwo(List<?> dataList,
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

        SummarizationState summarizationState =
                new SummarizationState(
                        qualifiers,
                        quantifiers,
                        summarizers,
                        intersectSet);

        /*double finalSizeNormalized = calculateNormalized(intersectSet);
        System.out.println(finalSizeNormalized);*/

        Tuple<String, Double> nameMaxTuple = this.extractNameAndMax(quantifiers, DegreeOfTruthT1.calculateR(dataList, summarizationState));
        double max = nameMaxTuple.getSecond();
        String name = nameMaxTuple.getFirst();

        String summaryContent =
                this.buildSummaryContext(String.format("%s %s, które mają/są ", name, this.entityName), qualifiers);
        summaryContent += this.buildSummaryContext(" ma/jest ", summarizers);

        Map<String, Double> qualityMeasures = calculateQualityMeasures(dataList, summarizationState, name);

        return new Tuple<>(
                new Summary(summaryContent, max,
                        qualityMeasures.get("T2"),
                        qualityMeasures.get("T3"),
                        qualityMeasures.get("T4"),
                        qualityMeasures.get("T5"),
                        qualityMeasures.get("T6"),
                        qualityMeasures.get("T7"),
                        qualityMeasures.get("T8"),
                        qualityMeasures.get("T9"),
                        qualityMeasures.get("T10"),
                        qualityMeasures.get("T11")),
                new SummarizationState(
                        qualifiers,
                        quantifiers,
                        summarizers,
                        intersectSet
                )
        );
    }


    //HELPER METHODS

    /*private double calculateNormalized(FuzzySet intersectedSet) {
        double finalSizeNormalized = 0;
        if (intersectedSet.size() != 0) {
            finalSizeNormalized = FuzzySet.sumWithCardinality(intersectedSet,1) * 1.0 / intersectedSet.size();
        }
        return finalSizeNormalized;
    }*/

    private String buildSummaryContext(String beginning, List<? extends LinguisticVariable> lvs) {
        for (LinguisticVariable summarizer : lvs) {
            beginning += (summarizer.getName());
            if (lvs.indexOf(summarizer) <= lvs.size() - 2)
                beginning += (" i ");
        }
        return beginning;
    }

    private Tuple<String, Double> extractNameAndMax(List<? extends Quantifier> lvs, double r) {
        double max = -1.0;
        String name = "";
        for (Quantifier q : lvs) {
            if (q.calculate(r) > max) {
                max = q.calculate(r);
                name = q.getName();
            }
        }
        return new Tuple<>(name, max);
    }

    private FuzzySet waterfallIntersect(List<FuzzySet> fuzzySets) {
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

    private static Map<String, Double> calculateQualityMeasures(List<?> Data, SummarizationState summarizationState, String name) {
        HashMap<String, Double> QualityValuesMap = new HashMap<>();

        QualityValuesMap.put("T2", DegreeOfImprecisionT2.calculateDegreeOfImprecision(summarizationState));
        QualityValuesMap.put("T3", DegreeOfCoveringT3.calculateDegreeOfCovering(Data, summarizationState));
        QualityValuesMap.put("T4", DegreeOfApproppriatenessT4.calculateDegreeOfAppropriateness(Data, summarizationState, QualityValuesMap.get("T3")));
        QualityValuesMap.put("T5", LengthOfSummaryT5.calculateLengthOfSummaryT5(summarizationState));
        QualityValuesMap.put("T6", DegreeOfQuantifierImprecisionT6.calculateDegreeOfQuantifierImprecision(summarizationState, name));
        QualityValuesMap.put("T7", DegreeOfQuantifierCardinalityT7.calculateDegreeOfQuantifierCardinality(summarizationState, name));
        QualityValuesMap.put("T8", DegreeOfSummarizerCardinalityT8.calculateDegreeOfSummarizerCardinality(Data, summarizationState));
        QualityValuesMap.put("T9", DegreeOfQualifierImprecisionT9.calculateDegreeOfQualifierImprecision(summarizationState));
        QualityValuesMap.put("T10", DegreeOfQualifierCardinalityT10.calculateDegreeOfQualifierCardinality(Data, summarizationState));
        QualityValuesMap.put("T11", LengthOfQualifierT11.calculateLengthOfQualifierT11(summarizationState));

        return QualityValuesMap;
    }


}



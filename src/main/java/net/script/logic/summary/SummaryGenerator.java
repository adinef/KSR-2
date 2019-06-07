package net.script.logic.summary;

import net.script.data.entities.DCResMeasurement;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.view.Summary;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SummaryGenerator {

    private static List<Summary> summaries = new ArrayList<>();

    public static List<Summary> createSummary(Iterable<DCResMeasurement> dcResMeasurements, List<Quantifier> quantifiers, List<Qualifier> qualifiers, List<Summarizer> summarizers) {
        summaries.clear();
        Iterator<DCResMeasurement> source = dcResMeasurements.iterator();
        List<DCResMeasurement> DCList = new ArrayList<>();
        source.forEachRemaining(DCList::add);
        //TYPU PIERWSZEGO
        //pojedynczy sumaryzator
        for (Summarizer s : summarizers) {
            summaries.add(SummaryGenerator.createSummaryFirstType(DCList, quantifiers, s));
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
                        summaries.add(SummaryGenerator.createSummaryFirstTypeMultipleSummarizers(DCList, quantifiers, tempSumList));
                    }
                }
            }
            //wszystkie jeśli dotyczą innych kategorii
            List<Summarizer> uniqueCategorySummarizers = summarizers.stream().filter(distinctByKey(Summarizer::getMemberFieldName)).collect(Collectors.toList());
            if (summarizers.size() == uniqueCategorySummarizers.size() && summarizers.size() > 2) {
                summaries.add(SummaryGenerator.createSummaryFirstTypeMultipleSummarizers(DCList, quantifiers, summarizers));
            }
        }
        //TYPU DRUGIEGO PERMUTACJE
        //pojedynczy sumaryzator i kwalifikator
        if (qualifiers.size() > 0) {
            for (Summarizer s : summarizers) {
                for (Qualifier q : qualifiers) {
                    summaries.add(SummaryGenerator.createSummaryTypeTwo(DCList, quantifiers, Collections.singletonList(q), Collections.singletonList(s)));
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
                                summaries.add(SummaryGenerator.createSummaryTypeTwo(DCList, quantifiers, Collections.singletonList(q), tempSumList));
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
                                summaries.add(SummaryGenerator.createSummaryTypeTwo(DCList, quantifiers, tempQualList, Collections.singletonList(s)));
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
                                    summaries.add(SummaryGenerator.createSummaryTypeTwo(DCList, quantifiers, tempQualList, tempSumList));
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
                summaries.add(SummaryGenerator.createSummaryTypeTwo(DCList, quantifiers, qualifiers, summarizers));
            }
        }
        return summaries;
    }

    private static Summary createSummaryFirstType(List<DCResMeasurement> DCList, List<Quantifier> quantifiers, Summarizer summarizer) {
        FuzzySet summarizersSet = FuzzySet.with(DCList).from(summarizer);
        double finalSizeNormalized = 0;
        if (summarizersSet.size() != 0) {
            finalSizeNormalized = summarizersSet.support().size() * 1.0 / summarizersSet.size();
        }
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
        String podsumowanie = name + " budynków ma/jest ";
        podsumowanie += summarizer.getName();
        s.setContent(podsumowanie);
        s.setDegreeOfTruth(max);
        return s;
    }

    private static Summary createSummaryFirstTypeMultipleSummarizers(List<DCResMeasurement> DCList, List<Quantifier> quantifiers, List<Summarizer> summarizers) {
        List<FuzzySet> summarySets = new ArrayList<>();
        for (Summarizer summarizer : summarizers) {
            summarySets.add(FuzzySet.with(DCList).from(summarizer));
        }
        FuzzySet summarizersSet = summarySets.get(0);
        if (summarySets.size() >= 2) {
            for (int i = 1; i < summarySets.size(); i++) {
                summarizersSet = FuzzySet.intersect(summarizersSet, summarySets.get(i));
            }
        }
        double finalSizeNormalized = 0;
        if (summarizersSet.size() != 0) {
            finalSizeNormalized = summarizersSet.support().size() * 1.0 / summarizersSet.size();
        }
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
        StringBuilder summaryContent = new StringBuilder(name + " budynków ma/jest ");
        for (Summarizer summarizer : summarizers) {
            summaryContent.append(summarizer.getName());
            if (summarizers.indexOf(summarizer) <= summarizers.size() - 2)
                summaryContent.append(" i ");
        }
        s.setContent(summaryContent.toString());
        s.setDegreeOfTruth(max);
        return s;
    }

    private static Summary createSummaryTypeTwo(List<DCResMeasurement> DCList, List<Quantifier> quantifiers, List<Qualifier> qualifiers, List<Summarizer> summarizers) {
        List<FuzzySet> calculatedSets = new ArrayList<>();
        for (Qualifier qualifier : qualifiers) {
            calculatedSets.add(FuzzySet.with(DCList).from(qualifier));
        }
        FuzzySet intersectSet = calculatedSets.get(0);
        if (calculatedSets.size() >= 2) {
            for (int i = 1; i < calculatedSets.size(); i++) {
                intersectSet = FuzzySet.intersect(intersectSet, calculatedSets.get(i));
            }
        }
        calculatedSets.clear();
        for (Summarizer summarizer : summarizers) {
            calculatedSets.add(FuzzySet.with(DCList).from(summarizer));
        }
        for (FuzzySet calculatedSet : calculatedSets) {
            intersectSet = FuzzySet.intersect(intersectSet, calculatedSet);
        }
        double finalSizeNormalized = 0;
        if (intersectSet.size() != 0) {
            finalSizeNormalized = intersectSet.support().size() * 1.0 / intersectSet.size();
        }
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
        StringBuilder summaryContent = new StringBuilder(name + " budynków, które mają/są ");
        for (Qualifier qualifier : qualifiers) {
            summaryContent.append(qualifier.getName());
            if (qualifiers.indexOf(qualifier) <= qualifiers.size() - 2)
                summaryContent.append(" i ");
        }
        summaryContent.append(" ma/jest ");
        for (Summarizer summarizer : summarizers) {
            summaryContent.append(summarizer.getName());
            if (summarizers.indexOf(summarizer) <= summarizers.size() - 2)
                summaryContent.append(" i ");
        }

        s.setContent(summaryContent.toString());
        s.setDegreeOfTruth(max);
        return s;
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

}



package net.script.logic.summary.qualities;

import net.script.data.Tuple;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.SummarizationState;
import net.script.view.Summary;

import java.util.List;

public interface QualityMeasure {

    double calculateQualityValue(List<?> Data, SummarizationState StateSummaryTuple);

}

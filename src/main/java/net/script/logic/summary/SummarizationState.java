package net.script.logic.summary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;

import java.util.List;

@Getter
@AllArgsConstructor
public class SummarizationState {
    private List<Qualifier> qualifiers;
    private List<Quantifier> quantfifiers;
    private List<Summarizer> summarizers;
    private FuzzySet finalFuzzySet;
}

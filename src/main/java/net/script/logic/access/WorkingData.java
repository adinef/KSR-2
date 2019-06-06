package net.script.logic.access;

import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkingData {

    private List<Qualifier> qualifiers;
    private List<Quantifier> quantifiers;

    public void workingQualifiers(List<Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public List<Qualifier> workingQualifiers() {
        return this.qualifiers;
    }

    public void workingQuantifiers(List<Quantifier> quantifiers) {
        this.quantifiers = quantifiers;
    }

    public List<Quantifier> workingQuantifiers() {
        return this.quantifiers;
    }
}

package net.script.logic.access;

import net.script.data.FieldColumnTuple;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class WorkingData {

    private List<Qualifier> qualifiers;
    private List<Quantifier> quantifiers;

    public void setWorkingQualifiers(List<Qualifier> qualifiers) {
        this.qualifiers = qualifiers;
    }

    public List<Qualifier> workingQualifiers() {
        return this.qualifiers;
    }

    public List<Qualifier> workingQualifiers(List<FieldColumnTuple> tuples) {
        List<Qualifier> filtered = new LinkedList<>();
        List<Qualifier> qualifiers = workingQualifiers();
        if (qualifiers != null) {
            for (Qualifier qualifier : qualifiers) {
                if (tuples.stream().anyMatch( (e) -> e.getColumn().value().equals(qualifier.getMemberFieldName()) )) {
                    filtered.add(qualifier);
                }
            }
        }
        return filtered;
    }

    public void setWorkingQuantifiers(List<Quantifier> quantifiers) {
        this.quantifiers = quantifiers;
    }

    public List<Quantifier> workingQuantifiers() {
        return this.quantifiers;
    }

    public List<Quantifier> workingQuantifiers(List<FieldColumnTuple> tuples) {
        List<Quantifier> filtered = new LinkedList<>();
        List<Quantifier> quantifiers = workingQuantifiers();
        if (quantifiers != null) {
            for (Quantifier quantifier : quantifiers) {
                if (tuples.stream().anyMatch( (e) -> e.getColumn().value().equals(quantifier.getMemberFieldName()) )) {
                    filtered.add(quantifier);
                }
            }
        }
        return filtered;
    }
}

package net.script.logic.access;

import net.script.data.FieldColumnTuple;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class WorkingData {

    private List<Qualifier> qualifiers;
    private List<Quantifier> quantifiers;
    private List<Summarizer> summarizers;

    public void setWorkingQualifiers(List<Qualifier> qualifiers) {
        this.setWorkingQualifiers(qualifiers, false);
    }

    public void setWorkingQualifiers(List<Qualifier> qualifiers, boolean setIfEmpty) {
        if (setIfEmpty) {
            if (this.qualifiers == null) {
                this.qualifiers = qualifiers;
            }
        } else {
            this.qualifiers = qualifiers;
        }
    }

    public List<Qualifier> workingQualifiers() {
        return this.qualifiers;
    }

    public void setWorkingSummarizers(List<Summarizer> summarizers) {
        this.setWorkingQualifiers(qualifiers, false);
    }

    public void setWorkingSummarizers(List<Summarizer> summarizers, boolean setIfEmpty) {
        if (setIfEmpty) {
            if (this.qualifiers == null) {
                this.summarizers = summarizers;
            }
        } else {
            this.summarizers = summarizers;
        }
    }

    public List<Summarizer> workingSummarizers() {
        return this.summarizers;
    }

    public List<Summarizer> workingSummarizers(List<FieldColumnTuple> tuples) {
        List<Summarizer> filtered = new LinkedList<>();
        List<Summarizer> summarizers = workingSummarizers();
        if (qualifiers != null) {
            for (Summarizer summarizer : summarizers) {
                if (tuples.stream().anyMatch( (e) -> e.getColumn().value().equals(summarizer.getMemberFieldName()) )) {
                    filtered.add(summarizer);
                }
            }
        }
        return filtered;
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
        this.setWorkingQuantifiers(quantifiers, false);
    }

    public void setWorkingQuantifiers(List<Quantifier> quantifiers, boolean setIfEmpty) {
        if (setIfEmpty) {
            if (this.quantifiers == null) {
                this.quantifiers = quantifiers;
            }
        } else {
            this.quantifiers = quantifiers;
        }
    }

    public List<Quantifier> workingQuantifiers() {
        return this.quantifiers;
    }
}

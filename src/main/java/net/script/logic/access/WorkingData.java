package net.script.logic.access;

import lombok.extern.slf4j.Slf4j;
import net.script.data.FieldColumnTuple;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class WorkingData {

    private List<Qualifier> qualifiers;
    private List<Quantifier> quantifiers;
    private List<Summarizer> summarizers;

    @Autowired
    private FuzzyData fuzzyData;

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
        if (this.qualifiers == null) {
            try {
                this.qualifiers = fuzzyData.qualifiers();
            } catch (Exception e) {
                log.error(e.getMessage());
                this.qualifiers = new LinkedList<>();
            }
        }
        return this.qualifiers;
    }

    public void setWorkingSummarizers(List<Summarizer> summarizers) {
        this.setWorkingSummarizers(summarizers, false);
    }

    public void setWorkingSummarizers(List<Summarizer> summarizers, boolean setIfEmpty) {
        if (setIfEmpty) {
            if (this.summarizers == null) {
                this.summarizers = summarizers;
            }
        } else {
            this.summarizers = summarizers;
        }
    }

    public List<Summarizer> workingSummarizers() {
        if (this.summarizers == null) {
            try {
                this.summarizers = fuzzyData.summarizers();
            } catch (Exception e) {
                log.error(e.getMessage());
                this.summarizers = new LinkedList<>();
            }
        }
        return this.summarizers;
    }

    public List<Summarizer> workingSummarizers(List<FieldColumnTuple> tuples) {
        List<Summarizer> filtered = new LinkedList<>();
        List<Summarizer> summarizers = workingSummarizers();
        if (summarizers != null) {
            for (Summarizer summarizer : summarizers) {
                if (tuples.stream().anyMatch((e) -> e.getColumn().value().equals(summarizer.getMemberFieldName()))) {
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
                if (tuples.stream().anyMatch((e) -> e.getColumn().value().equals(qualifier.getMemberFieldName()))) {
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
        if (this.qualifiers == null) {
            try {
                this.quantifiers = fuzzyData.quantifiers();
            } catch (Exception e) {
                log.error(e.getMessage());
                this.quantifiers = new LinkedList<>();
            }
        }
        return this.quantifiers;
    }
}

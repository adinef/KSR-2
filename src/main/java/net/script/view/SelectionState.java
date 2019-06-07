package net.script.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import net.script.data.FieldColumnTuple;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;

@Data
public class SelectionState {
    private ObservableList<Quantifier> quantifiers = FXCollections.emptyObservableList();
    private ObservableList<Qualifier> qualifiers = FXCollections.emptyObservableList();
    private ObservableList<Summarizer> summarizers = FXCollections.emptyObservableList();
    private ObservableList<FieldColumnTuple> allowedFields = FXCollections.emptyObservableList();

    public boolean isAllSelected() {
        return !qualifiers.isEmpty() && !qualifiers.isEmpty() && !summarizers.isEmpty() && !allowedFields.isEmpty();
    }

    public boolean firstTypeReady() {
        return !quantifiers.isEmpty() && !summarizers.isEmpty() && !allowedFields.isEmpty();
    }

    public boolean secondTypeReady() {
        return isAllSelected();
    }
}

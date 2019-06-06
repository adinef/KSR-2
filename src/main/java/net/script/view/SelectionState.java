package net.script.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import net.script.data.FieldColumnTuple;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;

@Data
public class SelectionState {
    private ObservableList<Quantifier> quantifiers = FXCollections.emptyObservableList();
    private ObservableList<Qualifier> qualifiers = FXCollections.emptyObservableList();
    private ObservableList<FieldColumnTuple> allowedFields = FXCollections.emptyObservableList();
}

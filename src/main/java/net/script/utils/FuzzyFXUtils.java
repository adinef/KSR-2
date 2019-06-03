package net.script.utils;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.utils.JFXUtilities;
import com.jfoenix.validation.DoubleValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.script.data.annotations.Coefficient;
import net.script.data.annotations.Column;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.qualifier.Qualifier;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class FuzzyFXUtils {

    public static final String PROPER_NUMBER_PATTERN = "\\d{0,7}([\\.]\\d{0,4})?";

    public static <T extends LinguisticVariable> Optional editLVPopup(String title, T elem, Scene scene) {
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton closeButton = new JFXButton("Zamknij");

        VBox vBox = new VBox();

        List<Node> paramsForName = newFormParamString("Nazwa", elem::getName, elem::setName);
        List<Node> paramsForMember = newFormParamString("Pole", elem::getMemberFieldName, elem::setMemberFieldName);
        List<Node> paramsForRangeStart = newFormParamDouble("Zasięg - początek",
                () -> elem.getLvRange().getBegin().toString(), (nv) -> elem.getLvRange().setBegin(nv));
        List<Node> paramsForRangeEnd = newFormParamDouble("Zasięg - koniec",
                () -> elem.getLvRange().getEnd().toString(), (nv) -> elem.getLvRange().setEnd(nv));
        Label functionLabel = new Label("Wartości funkcji przynależności");
        List<Node> editFuncVarNodes = getNodesForFuncEdit(elem.getFunction());

        vBox.getChildren().addAll(paramsForName);
        vBox.getChildren().addAll(paramsForMember);
        vBox.getChildren().addAll(paramsForRangeStart);
        vBox.getChildren().addAll(paramsForRangeEnd);
        vBox.getChildren().addAll(functionLabel);
        vBox.getChildren().addAll(editFuncVarNodes);

        closeButton.setButtonType(JFXButton.ButtonType.FLAT);
        closeButton.setOnAction(event -> alert.hideWithAnimation());

        layout.setHeading(new Label(title));
        layout.setBody(vBox);
        layout.setActions(closeButton);
        alert.setAnimation(JFXAlertAnimation.TOP_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        alert.setContent(layout);
        return alert.showAndWait();
    }

    private static List<Node> getNodesForFuncEdit(QFunction function) {
        Field[] declaredFields = function.getClass().getDeclaredFields();
        List<Node> nodes = new LinkedList<>();
        for (Field field : declaredFields) {
            Coefficient coefficient = field.getAnnotation(Coefficient.class);
            Label label;
            if (coefficient != null) {
                label = new Label(coefficient.value());
            } else {
                label = new Label(field.getName());
            }
            field.setAccessible(true);
            JFXTextField textField;
            try {
                textField = new JFXTextField(
                        (
                                field.get(function) != null ?
                                        field.get(function).toString() :
                                        ""
                        )
                );

                addListenOnNumber(textField, (d) -> {
                    try {
                        field.setDouble(function, d);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IllegalAccessException e) {
                continue;
            }
            nodes.add(label);
            nodes.add(textField);
        }
        return nodes;
    }

    private static <T> List<Node> newFormParamString(String name, Supplier<T> valSupplier, Consumer<String> newValConsumer) {
        Label label = new Label(name);
        JFXTextField textField = new JFXTextField(valSupplier.get().toString());
        addListenerOnRegular(textField, newValConsumer::accept);
        return Arrays.asList(label, textField);
    }

    private static <T> List<Node> newFormParamDouble(String name, Supplier<T> valSupplier, Consumer<Double> newValConsumer) {
        Label label = new Label(name);
        JFXTextField textField = new JFXTextField(valSupplier.get().toString());
        addListenOnNumber(textField, newValConsumer::accept);
        return Arrays.asList(label, textField);
    }

    private static void addListenOnNumber(JFXTextField textField, Consumer<Double> doubleConsumer) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(PROPER_NUMBER_PATTERN)) {
                doubleConsumer.accept(Double.parseDouble(oldValue));
            } else {
                doubleConsumer.accept(Double.parseDouble(newValue));
            }
        });
    }

    private static void addListenerOnRegular(JFXTextField rangeEnd, Consumer<String> stringConsumer) {
        rangeEnd.textProperty().addListener((observable, oldValue, newValue) -> {
            stringConsumer.accept(newValue);
        });
    }
}

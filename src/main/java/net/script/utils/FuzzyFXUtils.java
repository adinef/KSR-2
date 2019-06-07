package net.script.utils;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.script.data.FieldColumnTuple;
import net.script.data.Named;
import net.script.data.annotations.Coefficient;
import net.script.data.annotations.Column;
import net.script.data.annotations.Function;
import net.script.logic.fuzzy.functions.FunctionSetting;
import net.script.logic.fuzzy.functions.QFunction;
import net.script.logic.fuzzy.functions.factory.QFunctionFactory;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.fuzzy.linguistic.Range;
import net.script.logic.quantifier.Quantifier;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class FuzzyFXUtils {

    public static final String PROPER_NUMBER_PATTERN = "\\d{0,7}([\\.]\\d{0,4})?";

    public static <T> ObservableList<FieldColumnTuple> selectFieldByClassPopup(Class<T> tClass, Scene scene, List<FieldColumnTuple> alreadySelected) {
        ObservableList<FieldColumnTuple> acceptableData = extractAcceptableData(tClass.getDeclaredFields());
        ObservableList<JFXCheckBox> checkBoxes =
                checkBoxesFor(acceptableData, alreadySelected, FieldColumnTuple::name);
        VBox mainVBox = new VBox();
        mainVBox.setMinHeight(500);
        mainVBox.setSpacing(10);
        mainVBox.getChildren().addAll(checkBoxes);
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton closeButton = new JFXButton("Zamknij");
        closeButton.setButtonType(JFXButton.ButtonType.FLAT);
        closeButton.setOnAction(event -> {
            alert.hideWithAnimation();
        });
        layout.setHeading(new Label("Wybierz pola"));
        layout.setBody(mainVBox);
        layout.setActions(closeButton);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        alert.setContent(layout);
        alert.showAndWait();

        ObservableList<FieldColumnTuple> selectedElements = FXCollections.observableArrayList();
        acceptableData
                .forEach((elem) -> {
                    Optional<JFXCheckBox> first = checkBoxes
                            .stream()
                            .filter((e) -> e.getText().equals(elem.name()))
                            .findFirst();
                    first.ifPresent((e) -> {
                        if (e.isSelected()) {
                            selectedElements.add(elem);
                        }
                    });
                });
        return selectedElements;
    }


    public static <T extends Named> List<T> checkBoxSelectAlert(List<T> inputData, Scene scene, List<T> alreadySelected) {

        ScrollPane insideScrollPane = new ScrollPane();
        JFXButton closeButton = new JFXButton("Zamknij");
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        closeButton.setButtonType(JFXButton.ButtonType.FLAT);
        closeButton.setOnAction(event -> {
            alert.hideWithAnimation();
        });
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));
        vBox.setMinWidth(400);
        insideScrollPane.setMinWidth(390);
        insideScrollPane.setMinHeight(500);
        insideScrollPane.setMaxHeight(500);
        ObservableList<JFXCheckBox> checkBoxes = checkBoxesFor(inputData, alreadySelected, Named::getName);
        vBox.getChildren().addAll(checkBoxes);
        vBox.getChildren().add(closeButton);
        insideScrollPane.setContent(vBox);
        alert.setContent(insideScrollPane);
        alert.showAndWait();

        ObservableList<T> selectedElements = FXCollections.observableArrayList();
        for (T elem : inputData) {
            Optional<JFXCheckBox> first = checkBoxes
                    .stream()
                    .filter((e) -> e.getText().equals(elem.getName()))
                    .findFirst();
            first.ifPresent((e) -> {
                if (e.isSelected()) {
                    selectedElements.add(elem);
                }
            });
        }
        return selectedElements;
    }


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

    public static Optional editQuantifierPopup(String title, Quantifier elem, Scene scene) {
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton closeButton = new JFXButton("Zamknij");

        VBox vBox = new VBox();

        List<Node> paramsForName = newFormParamString("Nazwa", elem::getName, elem::setName);
        Label functionLabel = new Label("Wartości funkcji przynależności");
        List<Node> editFuncVarNodes = getNodesForFuncEdit(elem.getFunction());

        vBox.getChildren().addAll(paramsForName);
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
        addListenerOnRegular(textField, newValConsumer);
        return Arrays.asList(label, textField);
    }

    private static <T> List<Node> newFormParamDouble(String name, Supplier<T> valSupplier, Consumer<Double> newValConsumer) {
        Label label = new Label(name);
        JFXTextField textField = new JFXTextField(valSupplier.get().toString());
        addListenOnNumber(textField, newValConsumer);
        return Arrays.asList(label, textField);
    }

    private static void addListenOnNumber(JFXTextField textField, Consumer<Double> doubleConsumer) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches(PROPER_NUMBER_PATTERN)) {
                textField.setText(oldValue);
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

    public static <T extends LinguisticVariable> Optional<T> newLinguisticVariablePopup(Class<T> tClass, Scene scene) {
        AtomicBoolean aborted = new AtomicBoolean(false);
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton closeButton = new JFXButton("Anuluj");
        JFXButton saveButton = new JFXButton("Zapisz");
        VBox vBox = new VBox();

        Constructor<T> constructor;
        try {
            constructor = tClass.getConstructor(String.class, String.class, QFunction.class, Range.class);
        } catch (NoSuchMethodException e) {
            log.error(e.getLocalizedMessage());
            return Optional.empty();
        }

        Range range = new Range(0D, 0D);

        T obj;

        try {
            obj = constructor.newInstance("", "", null, range);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getLocalizedMessage());
            return Optional.empty();
        }

        List<Node> paramsForName = newFormParamString("Nazwa", obj::getName, obj::setName);

        List<Node> paramsForMember = newFormParamString("Pole", obj::getMemberFieldName, obj::setMemberFieldName);

        T finalObj = obj;
        List<Node> paramsForRangeStart = newFormParamDouble("Zasięg - początek",
                () -> finalObj.getLvRange().getBegin().toString(), (nv) -> finalObj.getLvRange().setBegin(nv));

        List<Node> paramsForRangeEnd = newFormParamDouble("Zasięg - koniec",
                () -> finalObj.getLvRange().getEnd().toString(), (nv) -> finalObj.getLvRange().setEnd(nv));

        Label functionLabel = new Label("Funkcja przynależności");
        List<Class<? extends QFunction>> functionClasses = QFunctionFactory.functionTypes();
        ObservableList<FunctionParamsHolder> nameAndParamList = getNameAndParamList(functionClasses);
        JFXComboBox<FunctionParamsHolder> functionComboBox = new JFXComboBox<>(nameAndParamList);
        setConnverterForParamsHolder(nameAndParamList, functionComboBox);

        VBox funcEditBox = new VBox();
        functionComboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            funcEditBox.getChildren().clear();
            funcEditBox.getChildren().addAll( generateFunctionParametersEditBoxes(newVal));
        });

        vBox.getChildren().addAll(paramsForName);
        vBox.getChildren().addAll(paramsForMember);
        vBox.getChildren().addAll(paramsForRangeStart);
        vBox.getChildren().addAll(paramsForRangeEnd);
        vBox.getChildren().addAll(functionLabel);
        vBox.getChildren().addAll(functionComboBox);
        vBox.getChildren().addAll(funcEditBox);

        closeButton.setButtonType(JFXButton.ButtonType.FLAT);
        closeButton.setOnAction(event -> {
            aborted.lazySet(true);
            alert.hideWithAnimation();
        });

        saveButton.setButtonType(JFXButton.ButtonType.FLAT);
        saveButton.setOnAction(event -> {
            obj.setFunction(QFunctionFactory.getFunction(functionComboBox.getValue()));
            alert.hideWithAnimation();
        });

        layout.setHeading(new Label("Nowy element"));
        layout.setBody(vBox);
        layout.setActions(closeButton, saveButton);
        alert.setAnimation(JFXAlertAnimation.TOP_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        alert.setContent(layout);
        alert.showAndWait();

        if (aborted.get()) {
            return Optional.empty();
        } else {
            return Optional.of(obj);
        }
    }

    public static Optional<Quantifier> newQuantifierPopup(Scene scene) {
        AtomicBoolean aborted = new AtomicBoolean(false);
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton closeButton = new JFXButton("Anuluj");
        JFXButton saveButton = new JFXButton("Zapisz");
        VBox vBox = new VBox();

        Quantifier quantifier = new Quantifier("", null);

        List<Node> paramsForName = newFormParamString("Nazwa", quantifier::getName, quantifier::setName);

        Label functionLabel = new Label("Funkcja przynależności");
        List<Class<? extends QFunction>> functionClasses = QFunctionFactory.functionTypes();
        ObservableList<FunctionParamsHolder> nameAndParamList = getNameAndParamList(functionClasses);
        JFXComboBox<FunctionParamsHolder> functionComboBox = new JFXComboBox<>(nameAndParamList);
        setConnverterForParamsHolder(nameAndParamList, functionComboBox);

        VBox funcEditBox = new VBox();
        functionComboBox.valueProperty().addListener((observableValue, oldVal, newVal) -> {
            funcEditBox.getChildren().clear();
            funcEditBox.getChildren().addAll( generateFunctionParametersEditBoxes(newVal));
        });

        vBox.getChildren().addAll(paramsForName);
        vBox.getChildren().addAll(functionLabel);
        vBox.getChildren().addAll(functionComboBox);
        vBox.getChildren().addAll(funcEditBox);

        closeButton.setButtonType(JFXButton.ButtonType.FLAT);
        closeButton.setOnAction(event -> {
            aborted.lazySet(true);
            alert.hideWithAnimation();
        });

        saveButton.setButtonType(JFXButton.ButtonType.FLAT);
        saveButton.setOnAction(event -> {
            quantifier.setFunction(QFunctionFactory.getFunction(functionComboBox.getValue()));
            alert.hideWithAnimation();
        });

        layout.setHeading(new Label("Nowy element"));
        layout.setBody(vBox);
        layout.setActions(closeButton, saveButton);
        alert.setAnimation(JFXAlertAnimation.TOP_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        alert.setContent(layout);
        alert.showAndWait();

        if (aborted.get()) {
            return Optional.empty();
        } else {
            return Optional.of(quantifier);
        }
    }

    private static void setConnverterForParamsHolder(ObservableList<FunctionParamsHolder> nameAndParamList,
                                                     JFXComboBox<FunctionParamsHolder> functionComboBox) {
        functionComboBox.setConverter(
                new StringConverter<FunctionParamsHolder>() {
                    @Override
                    public String toString(FunctionParamsHolder functionParamsHolder) {
                        if (functionParamsHolder != null) {
                            return functionParamsHolder.functionName;
                        }
                        return null;
                    }

                    @Override
                    public FunctionParamsHolder fromString(String s) {
                        for (FunctionParamsHolder functionParamsHolder : nameAndParamList) {
                            if (functionParamsHolder.functionName.equals(s)) {
                                return functionParamsHolder;
                            }
                        }
                        return null;
                    }
                }
        );
    }

    private static ObservableList<Node> generateFunctionParametersEditBoxes(FunctionParamsHolder selectedItem) {
        Map<String, Double> coefficients = selectedItem.getCoefficients();
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> coeff : coefficients.entrySet()) {
            nodes.addAll(newFormParamDouble(coeff.getKey(), coeff::getValue, (d) -> coefficients.put(coeff.getKey(), d)));
        }
        return nodes;
    }

    private static ObservableList<FunctionParamsHolder> getNameAndParamList(List<Class<? extends QFunction>> functionClasses) {
        ObservableList<FunctionParamsHolder> paramsHolders = FXCollections.observableArrayList();
        for (Class<? extends QFunction> functionClass : functionClasses) {
            Function annotation = functionClass.getAnnotation(Function.class);
            if (annotation != null) {
                String name = annotation.value();
                HashMap<String, Double> coeffMapping = new HashMap<>();
                Field[] fields = functionClass.getDeclaredFields();
                for (Field field : fields) {
                    Coefficient coeff = field.getAnnotation(Coefficient.class);
                    if (coeff != null) {
                        coeffMapping.put(coeff.value(), 0D);
                    }
                }
                FunctionParamsHolder paramHolder = new FunctionParamsHolder();
                paramHolder.functionName = name;
                paramHolder.coefficients = coeffMapping;
                paramsHolders.add(paramHolder);
            }
        }
        return paramsHolders;
    }

    private static Optional<Column> extractColumn(Field field) {
        return Optional.of(field.getAnnotation(Column.class));
    }

    private static <T> ObservableList<JFXCheckBox> checkBoxesFor(List<T> inputData,
                                                                 List<T> alreadySelected,
                                                                 java.util.function.Function<T, String> nameProvider) {
        ObservableList<JFXCheckBox> checkBoxes = FXCollections.observableArrayList();
        inputData
                .forEach((e) -> {
                    JFXCheckBox checkBox = new JFXCheckBox(nameProvider.apply(e));
                    if (alreadySelected.contains(e)) {
                        checkBox.setSelected(true);
                    }
                    checkBoxes.add(checkBox);
                });
        return checkBoxes;
    }

    private static <T> ObservableList<FieldColumnTuple> extractAcceptableData(Field[] fields) {
        ObservableList<FieldColumnTuple> data = FXCollections.observableArrayList();
        Arrays
                .stream(fields)
                .forEach( (field) -> {
                    extractColumn(field)
                            .ifPresent( column -> {
                                data.add(new FieldColumnTuple(field, column));
                            });
                });
        return data;
    }

    @Data
    private static class FunctionParamsHolder implements FunctionSetting {
        private String functionName;
        private Map<String, Double> coefficients;

        @Override
        public String getName() {
            return functionName;
        }

        @Override
        public Map<String, Double> getCoefficients() {
            return coefficients;
        }
    }
}

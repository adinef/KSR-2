package net.script.utils;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.script.data.FieldColumnTuple;
import net.script.data.Named;
import net.script.data.Tuple;
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

    private static final String PROPER_NUMBER_PATTERN = "\\d{0,7}([\\.]\\d{0,4})?";

    public static <T> ObservableList<FieldColumnTuple> selectFieldByClassPopup(Class<T> tClass,
                                                                               Scene scene,
                                                                               List<FieldColumnTuple> alreadySelected,
                                                                               boolean fuzzyableOnly) {
        ObservableList<FieldColumnTuple> acceptableData = extractAcceptableData(tClass.getDeclaredFields(), fuzzyableOnly);
        ObservableList<JFXCheckBox> checkBoxes =
                checkBoxesFor(acceptableData, alreadySelected, FieldColumnTuple::name);
        VBox mainVBox = new VBox();
        mainVBox.setMinHeight(500);
        mainVBox.setSpacing(10);
        mainVBox.getChildren().addAll(checkBoxes);
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton closeButton = standardButton("Zamknij", (e) -> alert.hideWithAnimation());
        layout.setBody(mainVBox);
        layout.setHeading(new Label("Wybierz pola"));
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


    public static <T extends Named> Tuple<OperatorChoice, ObservableList<T>> checkBoxSelectAlert(
            List<T> inputData,
            Scene scene,
            Tuple<OperatorChoice, List<T>> alreadySelected,
            boolean withOperators) {

        ScrollPane insideScrollPane = new ScrollPane();

        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        alert.setWidth(700);
        JFXButton closeButton = standardButton("Zamknij", (e) -> alert.hideWithAnimation());
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));
        vBox.setMinWidth(400);
        insideScrollPane.setMinWidth(390);
        insideScrollPane.setMinHeight(500);
        insideScrollPane.setMaxHeight(500);
        ObservableList<JFXCheckBox> checkBoxes = checkBoxesFor(inputData, alreadySelected.getSecond(), Named::getName);
        vBox.getChildren().addAll(checkBoxes);
        insideScrollPane.setContent(vBox);
        VBox andOrBox = new VBox();
        andOrBox.setSpacing(10);
        andOrBox.setPadding(new Insets(20));
        andOrBox.setMinWidth(180);
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton andChosen = new RadioButton("Z operacją 'i'");
        andChosen.setToggleGroup(toggleGroup);
        andChosen.setSelected(alreadySelected.getFirst().isAndChosen());
        RadioButton orChosen = new RadioButton("Z operacją 'lub'");
        orChosen.setToggleGroup(toggleGroup);
        orChosen.setSelected(true);
        andChosen.setSelected(alreadySelected.getFirst().isAndChosen());
        andOrBox.getChildren().addAll(andChosen, orChosen);
        HBox choiceAllBox = new HBox();
        choiceAllBox.setSpacing(10);
        if (withOperators) {
            choiceAllBox.getChildren().addAll(insideScrollPane, andOrBox);
        } else {
            choiceAllBox.getChildren().addAll(insideScrollPane);
        }

        layout.setBody(choiceAllBox);
        layout.setActions(closeButton);
        alert.setContent(layout);
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
        return new Tuple<>( new OperatorChoice(andChosen.isSelected()), selectedElements);
    }


    public static <T extends LinguisticVariable> Optional editLVPopup(String title, T elem, Scene scene, Class<?> entityClass) {
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        VBox vBox = new VBox();

        List<Node> paramsForName = newFormParamString("Nazwa", elem::getName, elem::setName);
        List<Node> paramsForMember = newFormComboForMember("Pole", elem::getMemberFieldName, elem::setMemberFieldName, entityClass);
        List<Node> paramsForRangeStart = newFormParamDouble("Zasięg - początek",
                () -> elem.getLvRange().getBegin().toString(), (nv) -> elem.getLvRange().setBegin(nv));
        List<Node> paramsForRangeEnd = newFormParamDouble("Zasięg - koniec",
                () -> elem.getLvRange().getEnd().toString(), (nv) -> elem.getLvRange().setEnd(nv));
        Label functionLabel = new Label("Wartości funkcji przynależności");
        List<Node> editFuncVarNodes = getNodesForFuncEdit(elem.getFunction());
        fillVBox(vBox,
                paramsForName,
                paramsForMember,
                paramsForRangeStart,
                paramsForRangeEnd,
                functionLabel,
                editFuncVarNodes);
        JFXButton closeButton = standardButton("Zamknij", (e) -> alert.hideWithAnimation());
        closeButton.setDefaultButton(true);
        closeButton.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                closeButton.fire();
                e.consume();
            }
        });
        layout.setHeading(new Label(title));
        layout.setBody(vBox);
        layout.setActions(closeButton);
        alert.setAnimation(JFXAlertAnimation.TOP_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        alert.setContent(layout);
        return alert.showAndWait();
    }

    private static List<Node> newFormComboForMember(String name,
                                                    Supplier<String> getMemberFieldName,
                                                    Consumer<String> setMemberFieldName,
                                                    Class<?> entityClass) {
        Label label = new Label(name);
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        List<String> acceptable = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null) {
                acceptable.add(annotation.value());
            }
        }
        comboBox.getItems().addAll(acceptable);
        comboBox.getSelectionModel().select(getMemberFieldName.get());
        comboBox.valueProperty().addListener(
                (obc, oV, nV) -> {
                    setMemberFieldName.accept(nV);
                });
        return Arrays.asList(label, comboBox);
    }

    public static Optional editQuantifierPopup(String title, Quantifier elem, Scene scene) {
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();

        VBox vBox = new VBox();

        List<Node> paramsForName = newFormParamString("Nazwa", elem::getName, elem::setName);
        Label functionLabel = new Label("Wartości funkcji przynależności");
        fillVBox(vBox, paramsForName, functionLabel, getNodesForFuncEdit(elem.getFunction()));

        JFXButton closeButton = standardButton("Zamknij", (e) -> alert.hideWithAnimation());
        closeButton.setDefaultButton(true);
        closeButton.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                closeButton.fire();
                e.consume();
            }
        });
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

    public static <T extends LinguisticVariable> Optional<T> newLinguisticVariablePopup(Class<T> tClass, Scene scene, Class<?> entityClass) {
        AtomicBoolean aborted = new AtomicBoolean(false);
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        VBox vBox = new VBox();

        Constructor<T> constructor;
        try {
            constructor = tClass.getConstructor(String.class, String.class, QFunction.class, Range.class);
        } catch (NoSuchMethodException e) {
            log.error(e.getLocalizedMessage());
            return Optional.empty();
        }

        T obj;
        try {
            obj = constructor.newInstance("", "", null, new Range(0D, 0D));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error(e.getLocalizedMessage());
            return Optional.empty();
        }

        List<Node> paramsForName = newFormParamString("Nazwa", obj::getName, obj::setName);
        List<Node> paramsForMember = newFormComboForMember("Pole", obj::getMemberFieldName, obj::setMemberFieldName, entityClass);

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
            funcEditBox.getChildren().addAll(generateFunctionParametersEditBoxes(newVal));
        });

        fillVBox(vBox,
                paramsForName,
                paramsForMember,
                paramsForRangeStart,
                paramsForRangeEnd,
                functionLabel,
                functionComboBox,
                funcEditBox);

        JFXButton closeButton = standardButton("Anuluj", actionEvent -> {
            aborted.lazySet(true);
            alert.hideWithAnimation();
        });
        JFXButton saveButton = standardButton("Zapisz", actionEvent -> {
            obj.setFunction(QFunctionFactory.getFunction(functionComboBox.getValue()));
            alert.hideWithAnimation();
        });
        saveButton.setDefaultButton(true);
        saveButton.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveButton.fire();
                e.consume();
            }
        });
        layout.setHeading(new Label("Nowy element"));
        layout.setBody(vBox);
        layout.setActions(closeButton, saveButton);
        alert.setAnimation(JFXAlertAnimation.TOP_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setHideOnEscape(false);
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
            funcEditBox.getChildren().addAll(generateFunctionParametersEditBoxes(newVal));
        });
        fillVBox(vBox, paramsForName, functionLabel, functionComboBox, funcEditBox);

        JFXButton closeButton = standardButton("Anuluj", actionEvent -> {
            aborted.lazySet(true);
            alert.hideWithAnimation();
        });
        JFXButton saveButton = standardButton("Zapisz", actionEvent -> {
            quantifier.setFunction(QFunctionFactory.getFunction(functionComboBox.getValue()));
            alert.hideWithAnimation();
        });
        saveButton.setDefaultButton(true);
        saveButton.addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                saveButton.fire();
                e.consume();
            }
        });

        layout.setHeading(new Label("Nowy element"));
        layout.setBody(vBox);
        layout.setActions(closeButton, saveButton);
        alert.setAnimation(JFXAlertAnimation.TOP_ANIMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setOverlayClose(false);
        alert.setHideOnEscape(false);
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

    private static void fillVBox(VBox vBox, Object... data) {
        if (data != null) {
            for (Object elem : data) {
                if (elem instanceof List) {
                    vBox.getChildren().addAll((List) elem);
                }
                if (elem instanceof Node) {
                    vBox.getChildren().add((Node) elem);
                }
            }
        }
    }

    private static JFXButton standardButton(String name, Consumer<ActionEvent> consumer) {
        JFXButton button = new JFXButton(name);
        button.setButtonType(JFXButton.ButtonType.FLAT);
        button.setOnAction(consumer::accept);
        return button;
    }

    private static ObservableList<FieldColumnTuple> extractAcceptableData(Field[] fields, boolean fuzzyableOnly) {
        ObservableList<FieldColumnTuple> data = FXCollections.observableArrayList();
        Arrays
                .stream(fields)
                .forEach((field) -> {
                    extractColumn(field)
                            .ifPresent(column -> {
                                if (column.fuzzable()) {
                                    data.add(new FieldColumnTuple(field, column));
                                }
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

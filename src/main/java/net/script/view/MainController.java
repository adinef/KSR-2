package net.script.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import net.script.Main;
import net.script.data.FieldColumnTuple;
import net.script.data.Named;
import net.script.data.Tuple;
import net.script.data.annotations.Column;
import net.script.data.repositories.CachingRepository;
import net.script.logic.access.FuzzyData;
import net.script.logic.access.WorkingData;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.SummarizationState;
import net.script.logic.summary.SummaryGenerator;
import net.script.utils.*;
import net.script.utils.functional.ConsumerWithException;
import net.script.utils.functional.RunnableWithException;
import net.script.utils.functional.SupplierWithException;
import net.script.utils.tasking.EntityReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static net.script.data.annotations.enums.Author.*;

@Controller
@Slf4j
public class MainController implements Initializable {

    private final CachingRepository repository;
    private final FuzzyData fuzzyData;
    private final WorkingData workingData;
    private final SettingsPopup settingsPopup;
    private boolean isFullscreen;

    @FXML
    private HBox summaryDataChosenBox;

    @FXML
    private Tab tab1;

    @FXML
    private MenuItem saveQualifiersOption;

    @FXML
    private MenuItem saveQuantifiersOption;

    @FXML
    private MenuItem saveSummarizersOption;

    // ************** DATA ****************
    private SelectionState selectionState = new SelectionState();
    private SummaryGenerator summaryGenerator;
    // ************************************

    // ************** TableView to class mapping ****************
    private Map<String, TableView> tableViewMap = new HashMap<>();
    // **********************************************************

    //*************** Selected data elems mapping ***************
    private Map<Class, List<Node>> nodesMapping = new HashMap<>();
    // **********************************************************

    @Autowired
    public MainController(
            FuzzyData fuzzyData,
            WorkingData workingData,
            CachingRepository repository, SettingsPopup settingsPopup) {
        this.repository = repository;
        this.fuzzyData = fuzzyData;
        this.workingData = workingData;
        this.settingsPopup = settingsPopup;
    }

    @FXML
    public void initialize() {
        // initialize your data here
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initialize();
    }

    @FXML
    private void restoreWindow() {
        Main.getCurrentStage().setFullScreen(!isFullscreen);
        isFullscreen = !isFullscreen;
    }

    @FXML
    private void minimizeWindow() {
        Main.getCurrentStage().setIconified(true);
    }

    @FXML
    private void closeWindow() {
        Main.getCurrentStage().close();
        Platform.exit();
    }

    public void barClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            restoreWindow();
        }
    }

    @FXML
    private void about() {
        CommonFXUtils.noDataPopup(
                "Autorzy",
                String.format("Projekt zrealizowany przez %s (%s), %s (%s) ",
                        AdrianFijalkowski.fullName(), AdrianFijalkowski.indexNumber(),
                        BartoszGoss.fullName(), BartoszGoss.indexNumber()),
                Main.getCurrentStage().getScene()
        );
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void loadData() {
        this.newTabWithContent(
                repository.getItemClass(),
                "Dane",
                repository::findAll,
                false,
                false,
                null);
    }

    public void showQuantifiers() {
        this.showLinguisticData(
                Quantifier.class,
                "Kwantyfikatory",
                workingData::workingQuantifiers
        );
        this.saveQuantifiersOption.setDisable(false);
    }

    public void showQualifiers() {
        this.showLinguisticData(Qualifier.class,
                "Kwalifikatory",
                workingData::workingQualifiers
        );
        this.saveQualifiersOption.setDisable(false);
    }

    public void showSummarizers() {
        this.showLinguisticData(Summarizer.class,
                "Summaryzatory",
                workingData::workingSummarizers
        );
        this.saveSummarizersOption.setDisable(false);
    }

    @FXML
    private void saveQualifiers() {
        CommonFXUtils.longTaskWithMessages(
                fuzzyData::saveQualifiers,
                "Pomyślnie zapisano kwalifikatory",
                "Wystąpił błąd zapisu",
                Main.getCurrentStage().getScene());
    }

    @FXML
    private void saveQuantifiers() {
        CommonFXUtils.longTaskWithMessages(
                fuzzyData::saveQuantifiers,
                "Pomyślnie zapisano kwantyfikatory",
                "Wystąpił błąd zapisu",
                Main.getCurrentStage().getScene());
    }

    @FXML
    private void saveSummarizers() {
        CommonFXUtils.longTaskWithMessages(
                fuzzyData::saveSummarizers,
                "Pomyślnie zapisano saummaryzatory",
                "Wystąpił błąd zapisu",
                Main.getCurrentStage().getScene());
    }

    private double prefTabContentHeight() {
        return Main.getCurrentStage().getHeight() - 100;
    }

    @FXML
    private void selectQuantifiers(ActionEvent actionEvent) {
        this.selectData(
                () -> this.workingData.setWorkingQuantifiers(this.fuzzyData.quantifiers()),
                Quantifier.class,
                (list) -> selectionState.setQuantifiers(list),
                this.workingData::workingQuantifiers,
                () -> selectionState.getQuantifiers()
        );
        this.setListView(
                Quantifier.class,
                "Wybrane kwantyfikatory",
                () -> selectionState.getQuantifiers().stream().map(Quantifier::getName).collect(Collectors.toList())
        );
    }

    @FXML
    private void selectQualifiers(ActionEvent actionEvent) {
        this.selectData(
                () -> this.workingData.setWorkingQualifiers(this.fuzzyData.qualifiers()),
                Qualifier.class,
                (list) -> selectionState.setQualifiers(list),
                () -> this.workingData.workingQualifiers(selectionState.getAllowedFields()),
                () -> selectionState.getQualifiers()
        );
        this.setListView(
                Qualifier.class,
                "Wybrane kwalifikatory",
                () -> selectionState.getQualifiers().stream().map(Qualifier::getName).collect(Collectors.toList())
        );
    }

    @FXML
    private void selectSummarizers(ActionEvent actionEvent) {
        this.selectData(
                () -> this.workingData.setWorkingSummarizers(this.fuzzyData.summarizers()),
                Summarizer.class,
                (list) -> selectionState.setSummarizers(list),
                () -> this.workingData.workingSummarizers(selectionState.getAllowedFields()),
                () -> selectionState.getSummarizers()
        );
        this.setListView(
                Summarizer.class,
                "Wybrane sumaryzatory",
                () -> selectionState.getSummarizers().stream().map(Summarizer::getName).collect(Collectors.toList())
        );
    }

    private void setListView(Class<?> elemClass,
                             String title,
                             Supplier<List<String>> valuesSupplier) {
        List<Node> nodes;
        if (this.nodesMapping.containsKey(elemClass)) {
            nodes = this.nodesMapping.get(elemClass);
            nodes.clear();
        } else {
            nodes = new ArrayList<>();
            this.nodesMapping.put(elemClass, nodes);
        }
        VBox vBox = new VBox();
        Label label = new Label(title);
        JFXListView<String> list = new JFXListView<>();
        list.setMinHeight(500);
        List<String> data = valuesSupplier.get();
        if (!data.isEmpty()) {
            list.getItems().addAll(
                    data
            );
            vBox.getChildren().addAll(label, list);
            nodes.add(vBox);
        } else {
            this.nodesMapping.remove(elemClass);
        }

        this.summaryDataChosenBox.getChildren().clear();
        for (List<Node> valList : this.nodesMapping.values()) {
            this.summaryDataChosenBox.getChildren().addAll(valList);
        }
    }


    private <T extends Named> void selectData(RunnableWithException initializer,
                                              Class<T> objClass,
                                              Consumer<ObservableList<T>> selectionConsumer,
                                              Supplier<List<T>> workingDataSupplier,
                                              Supplier<List<T>> currentStateSupplier) {
        try {
            initializer.run();
        } catch (Exception e) {
            CommonFXUtils.noDataPopup(
                    "Błąd",
                    "Błąd w trakcie wyboru " + objClass.getName() + ". " + e.getLocalizedMessage(),
                    Main.getCurrentStage().getScene()
            );
            e.printStackTrace();
            return;
        }
        selectionConsumer.accept(
                FXCollections.observableList(
                        FuzzyFXUtils
                                .checkBoxSelectAlert(
                                        workingDataSupplier.get(),
                                        Main.getCurrentStage().getScene(),
                                        currentStateSupplier.get()
                                )
                )
        );
    }

    @FXML
    private void selectAcceptableFields(ActionEvent actionEvent) {
        selectionState.setAllowedFields(
                FuzzyFXUtils
                        .selectFieldByClassPopup(
                                (Class<?>) repository.getItemClass(),
                                Main.getCurrentStage().getScene(),
                                selectionState.getAllowedFields(),
                                true
                        )
        );
        this.setListView(
                FieldColumnTuple.class,
                "Wybrane pola",
                () -> selectionState.getAllowedFields().stream().map(FieldColumnTuple::name).collect(Collectors.toList())
        );
    }

    @FXML
    private void newQualifier(ActionEvent actionEvent) {
        this.newElement(
                () -> FuzzyFXUtils.newLinguisticVariablePopup(
                        Qualifier.class,
                        Main.getCurrentStage().getScene(),
                        this.repository.getItemClass()),
                (e) -> this.fuzzyData.qualifiers().add(e),
                this.saveQualifiersOption,
                Qualifier.class
        );
    }

    @FXML
    private void newSummarizer(ActionEvent actionEvent) {
        this.newElement(
                () -> FuzzyFXUtils.newLinguisticVariablePopup(
                        Summarizer.class,
                        Main.getCurrentStage().getScene(),
                        this.repository.getItemClass()),
                (e) -> this.fuzzyData.summarizers().add(e),
                this.saveSummarizersOption,
                Summarizer.class
        );
    }

    @FXML
    private void newQuantifier(ActionEvent actionEvent) {
        this.newElement(
                () -> FuzzyFXUtils.newQuantifierPopup(Main.getCurrentStage().getScene()),
                (e) -> this.fuzzyData.quantifiers().add(e),
                this.saveQuantifiersOption,
                Quantifier.class
        );
    }

    private <T> void newElement(Supplier<Optional<T>> elemSupplier,
                                ConsumerWithException<T> consumer,
                                MenuItem option,
                                Class<T> objectClass) {
        Optional<T> elem = elemSupplier.get();
        elem.ifPresent(
                (e) -> {
                    try {
                        consumer.consume(e);
                        option.setDisable(false);
                        TableView tableViewOrNull = this.tableViewMap
                                .getOrDefault(objectClass.getName(), null);
                        if (tableViewOrNull != null) {
                            log.info("updating tab for " + objectClass.getName());
                            tableViewOrNull.refresh();
                        }
                    } catch (Exception ex) {
                        CommonFXUtils.noDataPopup(
                                "Błąd",
                                "Wystapił błąd przy odczycie. " + ex.getLocalizedMessage(),
                                Main.getCurrentStage().getScene()
                        );
                    }
                }
        );
    }

    @FXML
    private void proceedWithSummarization(ActionEvent actionEvent) {

        List<Tuple<Summary, SummarizationState>> summaries = new ArrayList<>();
        // TEMPORARILY
        if (selectionState.firstTypeReady()) {
            //this.newTabWithContent(repository.getItemClass(), "Dane", repository::findAll);
            //FIRST TYPE SUMMARIZATION
            //List<Summary> summaries = new ArrayList<>();
            summaries.clear();
            summaries.addAll(
                    summarizer().createSummary(
                            repository.findAll(),
                            selectionState.getQuantifiers(),
                            selectionState.getQualifiers(),
                            selectionState.getSummarizers()
                    )
            );
            VBox vBox = this.newTabWithContent(
                    Summary.class,
                    "Podsumowania",
                    () -> FXCollections.observableList(
                            summaries.stream().map(Tuple::getFirst).collect(Collectors.toList())
                    ),
                    false,
                    false,
                    null
            );
            JFXButton saveButton = new JFXButton("Zapisz podsumowania");
            saveButton.setOnAction((e) -> this.saveSummaries(summaries));
            vBox.getChildren().add(0, saveButton);
        } else {
            CommonFXUtils.noDataPopup("Dane",
                    "Proszę wybierz wszystkie potrzebne dane do wygenerowania podsumowania.",
                    Main.getCurrentStage().getScene()
            );
        }
    }

    private void saveSummaries(List<Tuple<Summary, SummarizationState>> summaries) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Wybierz katalog");
        File file = fileChooser.showSaveDialog(Main.getCurrentStage());
        if (file != null) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    CommonFXUtils.noDataPopup(
                            "Błąd",
                            "Wystąpił błąd w trakcie zapisu. " + e.getMessage(),
                            Main.getCurrentStage().getScene()
                    );
                    e.printStackTrace();
                    return;
                }
            }
            String data = "";
            for (Tuple<Summary, SummarizationState> sState : summaries) {
                Summary summary = sState.getFirst();
                data += summary.getContent() + ", ";
                data += summary.getDegreeOfTruth() + ", ";
                data += summary.getDegreeOfImprecision() + ", ";
                data += summary.getDegreeOfCovering() + ", ";
                data += summary.getDegreOfAppropriateness() + ", ";
                data += summary.getLengthOfSummary() + ", ";
                data += summary.getDegreeOfQuantifierImprecision() + ", ";
                data += summary.getDegreeOfQuantifierCardinality() + ", ";
                data += summary.getDegreeOfSummarizerCardinality() + ", ";
                data += summary.getDegreeOfQualifierImprecision() + ", ";
                data += summary.getDegreeOfQualifierCardinality() + ", ";
                data += summary.getLengthOfQualifier() + "\n";
            }
            try {
                Files.write(file.toPath(), data.getBytes());
            } catch (IOException e) {
                CommonFXUtils.noDataPopup(
                        "Błąd",
                        "Wystąpił błąd w trakcie zapisu. " + e.getMessage(),
                        Main.getCurrentStage().getScene()
                );
                e.printStackTrace();
            }
        }
    }

    private SummaryGenerator summarizer() {
        if (this.summaryGenerator == null) {
            this.summaryGenerator = new SummaryGenerator("budynków");
        }
        return this.summaryGenerator;
    }

    // HELPER METHODS

    private <T> void showLinguisticData(Class<T> tClass, String tabname, SupplierWithException<List<T>> listSupplier) {
        ObservableList<T> read = FXCollections.observableArrayList();
        try {
            read = FXCollections.observableList(listSupplier.get());
        } catch (Exception e) {
            CommonFXUtils.noDataPopup("Wystapił błąd", e.getLocalizedMessage(), Main.getCurrentStage().getScene());
            e.printStackTrace();
        }
        ObservableList<T> finalRead = read;
        this.newTabWithContent(tClass, tabname, () -> finalRead, true, true, (deletedElem) -> {
            System.out.println(deletedElem);
            if (Qualifier.class.equals(tClass)) {
                this.workingData.workingQualifiers().remove(deletedElem);
                this.selectionState.getQualifiers().remove(deletedElem);
            } else if (Quantifier.class.equals(tClass)) {
                this.workingData.workingQuantifiers().remove(deletedElem);
                this.selectionState.getQuantifiers().remove(deletedElem);
            } else if (Summarizer.class.equals(tClass)) {
                this.workingData.workingSummarizers().remove(deletedElem);
                this.selectionState.getSummarizers().remove(deletedElem);
            }
        });
    }


    @SuppressWarnings("unchecked")
    private <T> VBox newTabWithContent(Class<T> tClass,
                                       String name,
                                       Supplier<Iterable<T>> dataSupplier,
                                       boolean editable,
                                       boolean deletable,
                                       Consumer<T> deleteConsumer) {
        EntityReadService<T> task = new EntityReadService<>(dataSupplier);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        StackPane stackPane = new StackPane();
        JFXSpinner jfxSpinner = new JFXSpinner();
        jfxSpinner.setRadius(50);
        TableView tableView = new TableView();
        vBox.getChildren().add(stackPane);
        stackPane.getChildren().addAll(tableView, jfxSpinner);
        tableView.setPrefHeight(prefTabContentHeight());
        List<TableColumn<String, T>> simpleColumns =
                CommonFXUtils.getSimpleColumnsForClass(tClass, false);
        setColumnToolTipIfAvailible(tClass, simpleColumns);
        tableView.getColumns().addAll(simpleColumns);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        ContextMenu contextMenu = null;
        if (editable) {
            contextMenu = new ContextMenu();
            MenuItem menuItem = new MenuItem("Edytuj");
            contextMenu.getItems().add(menuItem);
            menuItem.setOnAction((e) -> this.editContext(tableView));
            tableView.setOnMouseClicked((e) -> this.listenForTableDoubleClick(e, tableView));
        }
        if (deletable) {
            if (contextMenu == null) {
                contextMenu = new ContextMenu();
            }
            MenuItem menuItem = new MenuItem("Usuń");
            menuItem.setOnAction((e) -> this.deleteContext(deleteConsumer, tableView));
            contextMenu.getItems().add(menuItem);
            tableView.setOnKeyPressed((e) -> this.askForDelete(e, tableView, deleteConsumer));
        }
        if (contextMenu != null) {
            tableView.setContextMenu(contextMenu);
        }
        Tab e1 = new Tab(name, vBox);
        tab1.getTabPane().getTabs().add(e1);
        HBox hBox = new HBox();
        Label quantityLabel = new Label("Ilość elementów: ");
        Label actualQuantity = new Label("0");
        hBox.getChildren().addAll(quantityLabel, actualQuantity);
        vBox.getChildren().add(hBox);
        task.setOnSucceeded(
                e -> {
                    Collection data = (Collection) e.getSource().getValue();
                    tableView.setItems(FXCollections.observableList(new ArrayList<>(data)));
                    actualQuantity.setText(String.valueOf(data.size()));
                    stackPane.getChildren().remove(jfxSpinner);
                }
        );
        task.start();
        this.tableViewMap.put(tClass.getName(), tableView);
        return vBox;
    }

    private <T> void askForDelete(KeyEvent e, TableView<T> tableView, Consumer<T> deleteConsumer) {
        if (e.getCode() == KeyCode.DELETE) {
            this.deleteContext(deleteConsumer, tableView);
        }
    }

    private <T> void setColumnToolTipIfAvailible(Class<T> tClass, List<TableColumn<String, T>> simpleColumns) {
        for (TableColumn col : simpleColumns) {
            for (Field field : tClass.getDeclaredFields()) {
                Column colAnn = field.getAnnotation(Column.class);
                if (colAnn != null && !colAnn.tooltip().isEmpty()) {
                    if (colAnn.value().equals(col.getText())) {
                        Label label = new Label(col.getText());
                        label.setTooltip(new Tooltip(colAnn.tooltip()));
                        col.setGraphic(label);
                        col.setText("");
                    }
                }
            }
        }
    }

    private void listenForTableDoubleClick(MouseEvent mouseEvent, TableView tableView) {
        if (mouseEvent.getClickCount() == 2) {
            editContext(tableView);
        }
    }

    private <T> void deleteContext(Consumer<T> deleteConsumer, TableView<T> tableView) {
        boolean delete = CommonFXUtils
                .yesNoPopup("Usuwanie",
                        "Czy na pewno usunąć element?",
                        Main.getCurrentStage().getScene()
                );
        if (delete) {
            T selectedItem = tableView.getSelectionModel().getSelectedItem();
            deleteConsumer.accept(selectedItem);
            tableView.getItems().remove(selectedItem);
            tableView.refresh();
        }
    }

    private void editContext(TableView tableView) {
        Object selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem instanceof LinguisticVariable) {
            LinguisticVariable lv = (LinguisticVariable) selectedItem;
            Optional<?> editQualifierOptional =
                    FuzzyFXUtils.editLVPopup(
                            "Edytuj kwalifikator",
                            lv,
                            Main.getCurrentStage().getScene(),
                            this.repository.getItemClass()
                    );
            editQualifierOptional.ifPresent((e) -> tableView.refresh());
        }
        if (selectedItem instanceof Quantifier) {
            Quantifier quantifier = (Quantifier) selectedItem;
            Optional<?> editQuantifierOptional =
                    FuzzyFXUtils.editQuantifierPopup(
                            "Edytuj kwantyfikator",
                            quantifier,
                            Main.getCurrentStage().getScene()
                    );
            editQuantifierOptional.ifPresent((e) -> tableView.refresh());
        }
    }

    @FXML
    private void settings(ActionEvent actionEvent) {
        settingsPopup.show(Main.getCurrentStage().getScene());
    }
}

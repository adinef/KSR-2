package net.script.view;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;
import net.script.Main;
import net.script.data.Named;
import net.script.data.annotations.Column;
import net.script.data.entities.DCResMeasurement;
import net.script.data.repositories.CachingRepository;
import net.script.logic.access.FuzzyData;
import net.script.logic.access.WorkingData;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.logic.summary.SummaryGeneratorK;
import net.script.logic.summary.SummaryGeneratorY;
import net.script.utils.*;
import net.script.utils.functional.ConsumerWithException;
import net.script.utils.functional.RunnableWithException;
import net.script.utils.functional.SupplierWithException;
import net.script.utils.tasking.Barrier;
import net.script.utils.tasking.EntityReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.script.data.annotations.enums.Author.*;

@Controller
@Slf4j
public class MainController implements Initializable {

    private final CachingRepository repository;
    private final FuzzyData fuzzyData;
    private final WorkingData workingData;
    private boolean isFullscreen;

    private Barrier barrier = Barrier.of(3);

    @FXML
    private Tab tab1;

    @FXML
    private Button saveQualifiersButton;

    @FXML
    private Button saveQuantifiersButton;

    @FXML
    private Button selectQualifiersButton;

    @FXML
    private Button selectQuantifiersButton;

    @FXML
    private Button saveSummarizersButton;

    @FXML
    private Button selectSummarizersButton;

    @FXML
    private Button calculateButton;

    // ************** DATA ****************
    private SelectionState selectionState = new SelectionState();
    // ************************************

    // ************** TableView to class mapping ****************
    private Map<String, TableView> tableViewMap = new HashMap<>();
    // **********************************************************

    @Autowired
    public MainController(
            FuzzyData fuzzyData,
            WorkingData workingData,
            CachingRepository repository) {
        this.repository = repository;
        this.fuzzyData = fuzzyData;
        this.workingData = workingData;
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
                null);
    }

    public void showQuantifiers() {
        this.showLinguisticData(
                Quantifier.class,
                "Kwantyfikatory",
                workingData::workingQuantifiers
        );
        this.saveQuantifiersButton.setDisable(false);
    }

    public void showQualifiers() {
        this.showLinguisticData(Qualifier.class,
                "Kwalifikatory",
                workingData::workingQualifiers
        );
        this.saveQualifiersButton.setDisable(false);
    }

    public void showSummarizers() {
        this.showLinguisticData(Summarizer.class,
                "Summaryzatory",
                workingData::workingSummarizers
        );
        this.saveSummarizersButton.setDisable(false);
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
        System.out.println(currentStateSupplier.get());
        if (this.barrier.checkIn(objClass.getName())) {
            calculateButton.setDisable(false);
        }
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
        System.out.println(selectionState.getAllowedFields());
        this.selectQuantifiersButton.setDisable(false);
        this.selectQualifiersButton.setDisable(false);
        this.selectSummarizersButton.setDisable(false);
    }

    @FXML
    private void newQualifier(ActionEvent actionEvent) {
        this.newElement(
                () -> FuzzyFXUtils.newLinguisticVariablePopup(
                        Qualifier.class,
                        Main.getCurrentStage().getScene(),
                        this.repository.getItemClass()),
                (e) -> this.fuzzyData.qualifiers().add(e),
                this.saveQualifiersButton,
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
                this.saveSummarizersButton,
                Summarizer.class
        );
    }

    @FXML
    private void newQuantifier(ActionEvent actionEvent) {
        this.newElement(
                () -> FuzzyFXUtils.newQuantifierPopup(Main.getCurrentStage().getScene()),
                (e) -> this.fuzzyData.quantifiers().add(e),
                this.saveQuantifiersButton,
                Quantifier.class
        );
    }

    private <T> void newElement(Supplier<Optional<T>> elemSupplier,
                                ConsumerWithException<T> consumer,
                                Button connectedButton,
                                Class<T> objectClass) {
        Optional<T> elem = elemSupplier.get();
        elem.ifPresent(
                (e) -> {
                    try {
                        consumer.consume(e);
                        connectedButton.setDisable(false);
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
        // TEMPORARILY
        if (selectionState.isAllSelected()) {
            //this.newTabWithContent(repository.getItemClass(), "Dane", repository::findAll);
            //FIRST TYPE SUMMARIZATION
            List<Summary> summaries = new ArrayList<>();
            for(Summarizer s : selectionState.getSummarizers()) {
                summaries.add(SummaryGeneratorY.createSummaryFirstType(repository.findAll(),workingData.workingQuantifiers(),s));
            }
            this.newTabWithContent(Summary.class, "Podsumowania", ()->FXCollections.observableList(summaries),false,null);
        } else {
            CommonFXUtils.noDataPopup("Dane",
                    "Proszę wybierz wszystkie potrzebne dane do wygenerowania podsumowania.",
                    Main.getCurrentStage().getScene()
            );
        }
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
        this.newTabWithContent(tClass, tabname, () -> finalRead, true, (deletedElem) -> {
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
    private <T> void newTabWithContent(Class<T> tClass,
                                       String name,
                                       Supplier<Iterable<T>> dataSupplier,
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
        tableView.setOnMouseClicked((e) -> this.listenForTableDoubleClick(e, tableView));
        if (deletable) {
            tableView.setOnKeyPressed((e) -> this.askForDelete(e, tableView, deleteConsumer));
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
    }

    private <T> void askForDelete(KeyEvent e, TableView<T> tableView, Consumer<T> deleteConsumer) {
        if (e.getCode() == KeyCode.DELETE) {
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
    }

    private <T> void setColumnToolTipIfAvailible(Class<T> tClass, List<TableColumn<String, T>> simpleColumns) {
        for (TableColumn col : simpleColumns) {
            for (Field field : tClass.getDeclaredFields()) {
                Column colAnn = field.getAnnotation(Column.class);
                if (colAnn != null && !colAnn.tooltip().isEmpty()) {
                    if (colAnn.value().equals(col.getText())) {
                        Label label = new Label(col.getText());
                        label.setFont(new Font(12));
                        label.setTooltip(new Tooltip(colAnn.tooltip()));
                        col.setGraphic(label);
                    }
                }
            }
        }
    }

    private void listenForTableDoubleClick(MouseEvent mouseEvent, TableView tableView) {
        if (mouseEvent.getClickCount() == 2) {
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
    }
}

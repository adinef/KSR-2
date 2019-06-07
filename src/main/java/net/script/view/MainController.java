package net.script.view;

import com.jfoenix.controls.JFXSpinner;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import lombok.extern.slf4j.Slf4j;
import net.script.Main;
import net.script.data.Named;
import net.script.data.repositories.CachingRepository;
import net.script.logic.access.FuzzyData;
import net.script.logic.access.WorkingData;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.summarizer.Summarizer;
import net.script.utils.*;
import net.script.utils.functional.ConsumerWithException;
import net.script.utils.functional.RunnableWithException;
import net.script.utils.functional.SupplierWithException;
import net.script.utils.tasking.Barrier;
import net.script.utils.tasking.EntityReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

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
        this.newTabWithContent(repository.getItemClass(), "Dane", repository::findAll);
    }

    public void showQuantifiers() {
        this.showLinguisticData(
                Quantifier.class,
                "Kwantyfikatory",
                fuzzyData::quantifiers
        );
        this.saveQuantifiersButton.setDisable(false);
    }

    public void showQualifiers() {
        this.showLinguisticData(Qualifier.class,
                "Kwalifikatory",
                fuzzyData::qualifiers
        );
        this.saveQualifiersButton.setDisable(false);
    }

    public void showSummarizers() {
        this.showLinguisticData(Summarizer.class,
                "Summaryzatory",
                fuzzyData::summarizers
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
                                (Class<?>)repository.getItemClass(),
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
                () -> FuzzyFXUtils.newLinguisticVariablePopup(Qualifier.class, Main.getCurrentStage().getScene()),
                (e) -> this.fuzzyData.qualifiers().add(e),
                this.saveQualifiersButton,
                Qualifier.class
        );
    }
    @FXML
    private void newSummarizer(ActionEvent actionEvent) {
        this.newElement(
                () -> FuzzyFXUtils.newLinguisticVariablePopup(Summarizer.class, Main.getCurrentStage().getScene()),
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
                this.saveQualifiersButton,
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
        this.newTabWithContent(tClass, tabname, () -> finalRead);
    }


    @SuppressWarnings("unchecked")
    private <T> void newTabWithContent(Class<T> tClass, String name, Supplier<Iterable<T>> dataSupplier) {
        EntityReadService<T> task = new EntityReadService<>(dataSupplier);
        StackPane stackPane = new StackPane();
        JFXSpinner jfxSpinner = new JFXSpinner();
        jfxSpinner.setRadius(50);
        TableView tableView = new TableView();
        stackPane.getChildren().addAll(tableView, jfxSpinner);
        tableView.setPrefHeight(prefTabContentHeight());
        List<TableColumn<String, T>> simpleColumns =
                CommonFXUtils.getSimpleColumnsForClass(tClass, false);
        tableView.getColumns().addAll(simpleColumns);
        tableView.setOnMouseClicked((e) -> this.listenForTableDoubleClick(e, tableView));
        Tab e1 = new Tab(name, stackPane);
        tab1.getTabPane().getTabs().add(e1);
        task.setOnSucceeded(
                e -> {
                    Collection data = (Collection) e.getSource().getValue();
                    tableView.setItems(FXCollections.observableList(new ArrayList<>(data)));
                    stackPane.getChildren().remove(jfxSpinner);
                }
        );
        task.start();
        this.tableViewMap.put(tClass.getName(), tableView);
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
                                Main.getCurrentStage().getScene()
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

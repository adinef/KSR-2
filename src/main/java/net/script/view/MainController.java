package net.script.view;

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
import lombok.extern.slf4j.Slf4j;
import net.script.Main;
import net.script.data.annotations.enums.Author;
import net.script.data.repositories.CachingRepository;
import net.script.logic.access.FuzzyData;
import net.script.logic.access.WorkingData;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

@Controller
@Slf4j
public class MainController implements Initializable {

    private final CachingRepository repository;
    private final FuzzyData fuzzyData;
    private final WorkingData workingData;
    private boolean isFullscreen;

    private Barrier barrier = Barrier.of(2);

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
                        Author.AdrianFijalkowski.fullName(), Author.AdrianFijalkowski.indexNumber(),
                        Author.BartoszGoss.fullName(), Author.BartoszGoss.indexNumber()),
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
                Quantifier.class, "Kwantyfikatory",
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

    private double prefTabContentHeight() {
        return Main.getCurrentStage().getHeight() - 100;
    }

    @FXML
    private void selectQuantifiers(ActionEvent actionEvent) {
        try {
            this.workingData.setWorkingQuantifiers(this.fuzzyData.quantifiers());
        } catch (Exception e) {
            CommonFXUtils.noDataPopup(
                    "Błąd",
                    "Błąd w trakcie wyboru kwantyfikatorów. " + e.getLocalizedMessage(),
                    Main.getCurrentStage().getScene()
            );
            e.printStackTrace();
            return;
        }
        selectionState.setQuantifiers(
                FXCollections.observableList(
                        FuzzyFXUtils
                                .checkBoxSelectAlert(
                                        this.workingData.workingQuantifiers(),
                                        Main.getCurrentStage().getScene(),
                                        selectionState.getQuantifiers()
                                )
                )
        );
        System.out.println(selectionState.getQuantifiers());
        if (this.barrier.checkIn("sQf")) {
            calculateButton.setDisable(false);
        }
    }

    @FXML
    private void selectQualifiers(ActionEvent actionEvent) {
        try {
            this.workingData.setWorkingQualifiers(this.fuzzyData.qualifiers());
        } catch (Exception e) {
            CommonFXUtils.noDataPopup(
                    "Błąd",
                    "Błąd w trakcie wyboru kwalifikatorów. " + e.getLocalizedMessage(),
                    Main.getCurrentStage().getScene()
            );
            e.printStackTrace();
            return;
        }
        selectionState.setQualifiers(
                FXCollections.observableList(
                        FuzzyFXUtils
                                .checkBoxSelectAlert(
                                        this.workingData.workingQualifiers(selectionState.getAllowedFields()),
                                        Main.getCurrentStage().getScene(),
                                        selectionState.getQualifiers()
                                )
                )
        );
        System.out.println(selectionState.getQualifiers());
        if (this.barrier.checkIn("sQl")) {
            calculateButton.setDisable(false);
        }
    }

    @FXML
    private void selectAcceptableFields(ActionEvent actionEvent) {
        selectionState.setAllowedFields(
                FuzzyFXUtils
                        .selectFieldByClassPopup(
                                repository.getItemClass(),
                                Main.getCurrentStage().getScene(),
                                selectionState.getAllowedFields()
                        )
        );
        System.out.println(selectionState.getAllowedFields());
        this.selectQuantifiersButton.setDisable(false);
        this.selectQualifiersButton.setDisable(false);
    }

    @FXML
    private void newQualifier(ActionEvent actionEvent) {
            Optional<Qualifier> qualifier =
                    FuzzyFXUtils.newLinguisticVariablePopup(Qualifier.class, Main.getCurrentStage().getScene());
            qualifier.ifPresent(
                    (q) -> {
                        try {
                            this.fuzzyData.qualifiers().add(q);
                            this.saveQualifiersButton.setDisable(false);
                            TableView tableViewOrNull = this.tableViewMap
                                    .getOrDefault(Qualifier.class.getName(), null);
                            if (tableViewOrNull != null) {
                                log.info("updating tab for qualifiers");
                                tableViewOrNull.refresh();
                            }
                        } catch (Exception e) {
                            CommonFXUtils.noDataPopup(
                                    "Błąd",
                                    "Wystapił błąd przy odczycie kwalifikatorów. " + e.getLocalizedMessage(),
                                    Main.getCurrentStage().getScene()
                            );
                        }
                    }
            );
    }

    @FXML
    private void newQuantifier(ActionEvent actionEvent) {
        Optional<Quantifier> quantifier =
                FuzzyFXUtils.newQuantifierPopup(Main.getCurrentStage().getScene());
        quantifier.ifPresent(
                (q) -> {
                    try {
                        this.fuzzyData.quantifiers().add(q);
                        this.saveQuantifiersButton.setDisable(false);
                        TableView tableViewOrNull = this.tableViewMap
                                .getOrDefault(Quantifier.class.getName(), null);
                        if (tableViewOrNull != null) {
                            log.info("updating tab for quantifiers");
                            tableViewOrNull.refresh();
                        }
                    } catch (Exception e) {
                        CommonFXUtils.noDataPopup(
                                "Błąd",
                                "Wystapił błąd przy odczycie kwantyfikatorów. " + e.getLocalizedMessage(),
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
        TableView tableView = new TableView();
        tableView.setPrefHeight(prefTabContentHeight());
        List<TableColumn<String, T>> simpleColumns =
                CommonFXUtils.getSimpleColumnsForClass(tClass, false);
        tableView.getColumns().addAll(simpleColumns);
        tableView.setOnMouseClicked((e) -> this.listenForTableDoubleClick(e, tableView));
        Tab e1 = new Tab(name, tableView);
        tab1.getTabPane().getTabs().add(e1);
        task.setOnSucceeded(
                e -> {
                    Collection data = (Collection) e.getSource().getValue();
                    tableView.setItems(FXCollections.observableList(new ArrayList<>(data)));
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
                Optional editQualifierOptional =
                        FuzzyFXUtils.editLVPopup(
                                "Edytuj kwalifikator",
                                lv,
                                Main.getCurrentStage().getScene()
                        );
                editQualifierOptional.ifPresent((e) -> tableView.refresh());
            }
            if (selectedItem instanceof Quantifier) {
                Quantifier quantifier = (Quantifier) selectedItem;
                Optional editQuantifierOptional =
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

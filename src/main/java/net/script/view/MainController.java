package net.script.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import net.script.Main;
import net.script.config.main.ApplicationVariables;
import net.script.data.annotations.enums.Author;
import net.script.data.repositories.CachingRepository;
import net.script.logic.fuzzy.linguistic.LinguisticVariable;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.qualifier.QualifiersConfigAccessor;
import net.script.logic.settings.quantifier.QuantifiersConfigAccessor;
import net.script.utils.CommonFXUtils;
import net.script.utils.EntityReadService;
import net.script.utils.FuzzyFXUtils;
import net.script.utils.SupplierWithException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

@Controller
@Slf4j
public class MainController implements Initializable {

    private final QuantifiersConfigAccessor quantifiersReader;
    private final QualifiersConfigAccessor qualifiersReader;
    private final CachingRepository repository;
    private boolean isFullscreen;

    @FXML
    private Tab tab1;

    @Autowired
    public MainController(
            QuantifiersConfigAccessor quantifiersReader,
            QualifiersConfigAccessor qualifiersReader,
            CachingRepository repository) {
        this.quantifiersReader = quantifiersReader;
        this.qualifiersReader = qualifiersReader;
        this.repository = repository;
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
                () -> quantifiersReader.read(ApplicationVariables.determineRuntimeConfig())
        );
    }

    public void showQualifiers() {
        this.showLinguisticData(Qualifier.class,
                "Kwalifikatory",
                () -> qualifiersReader.read(ApplicationVariables.determineRuntimeConfig())
        );
    }


    private <T> void showLinguisticData(Class<T> tClass, String tabname, SupplierWithException<List<T>> listSupplier) {
        var ref = new Object() {
            ObservableList<T> read = FXCollections.observableArrayList();
        };
        try {
            ref.read = FXCollections.observableList(listSupplier.get());
        } catch (Exception e) {
            CommonFXUtils.noDataPopup("Wystapił błąd", e.getLocalizedMessage(), Main.getCurrentStage().getScene());
            e.printStackTrace();
        }
        this.newTabWithContent(tClass, tabname, () -> ref.read);
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
        tab1.getTabPane().getTabs().add(new Tab(name, tableView));
        task.setOnSucceeded(
                e -> {
                    Collection data = (Collection) e.getSource().getValue();
                    tableView.setItems(FXCollections.observableList(new ArrayList<>(data)));
                }
        );
        task.start();
    }

    private void listenForTableDoubleClick(MouseEvent mouseEvent, TableView tableView) {
        if (mouseEvent.getClickCount() == 2) {
            Object selectedItem = tableView.getSelectionModel().getSelectedItem();
            if (selectedItem instanceof LinguisticVariable) {
                LinguisticVariable lv = (LinguisticVariable)selectedItem;
                Optional editQualifierOptional =
                        FuzzyFXUtils.editLVPopup("Edytuj kwalifikator", lv, Main.getCurrentStage().getScene());
                editQualifierOptional.ifPresent((e) -> tableView.refresh());
            }
        }
    }

    @FXML
    private void saveQualifiers() {
        try {
            qualifiersReader.saveCachedData();
        } catch (Exception e) {
            e.printStackTrace();
            CommonFXUtils.noDataPopup(
                    "Błąd",
                    "Wystąpił błąd przy zapisie. " + e.getLocalizedMessage(),
                    Main.getCurrentStage().getScene()
            );
        }
    }

    @FXML
    private void saveQuantifiers() {
        try {
            quantifiersReader.saveCachedData();
        } catch (Exception e) {
            e.printStackTrace();
            CommonFXUtils.noDataPopup(
                    "Błąd",
                    "Wystąpił błąd przy zapisie. " + e.getLocalizedMessage(),
                    Main.getCurrentStage().getScene()
            );
        }
    }

    private double prefTabContentHeight() {
        return Main.getCurrentStage().getHeight() - 100;
    }


}

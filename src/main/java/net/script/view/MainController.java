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
import net.script.data.annotations.enums.Author;
import net.script.data.entities.DCResMeasurement;
import net.script.data.repositories.DCResMeasurementRepository;
import net.script.logic.qualifier.Qualifier;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.qualifier.QualifiersReader;
import net.script.logic.settings.quantifier.QuantifiersReader;
import net.script.utils.CommonFXUtils;
import net.script.utils.EntityReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

@Controller
@Slf4j
public class MainController implements Initializable {

    private final QuantifiersReader quantifiersReader;
    private final QualifiersReader qualifiersReader;
    private boolean isFullscreen;
    private final DCResMeasurementRepository repository;


    @FXML
    private Tab tab1;

    @Autowired
    public MainController(
            DCResMeasurementRepository repository,
            QuantifiersReader quantifiersReader,
            QualifiersReader qualifiersReader) {
        this.repository = repository;
        this.quantifiersReader = quantifiersReader;
        this.qualifiersReader = qualifiersReader;
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
                "Author",
                String.format("Project created by %s and %s on MIT licence.",
                        Author.AdrianFijalkowski.fullName() + " (" +
                                Author.AdrianFijalkowski.indexNumber() + "), ",
                        Author.BartoszGoss.fullName() + " (" +
                                Author.BartoszGoss.indexNumber() + ")"),
                Main.getCurrentStage().getScene()
        );
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void loadData() {
        TableView tableView = new TableView();
        List<TableColumn<String, DCResMeasurement>> simpleColumns =
                CommonFXUtils.getSimpleColumnsForClass(DCResMeasurement.class, false);
        tableView.getColumns().addAll(simpleColumns);
        tableView.setPrefHeight(700);
        this.fillColumns(tableView);
        tab1.getTabPane().getTabs().addAll( new Tab("Dane", tableView) );
    }

    @SuppressWarnings("unchecked")
    private void fillColumns(TableView tableView) {
        EntityReadService<DCResMeasurement> task = new EntityReadService<>(this.repository::findAll);
        ObservableList<DCResMeasurement> data = FXCollections.observableArrayList();
        task.setOnSucceeded((e) -> {
            data.addAll((Collection<? extends DCResMeasurement>) e.getSource().getValue());
            log.info("Data loaded");
            tableView.getItems().addAll(data);
        });
        task.start();
    }

    public void showQuantifiers() {
        ObservableList<Quantifier> read = FXCollections.observableArrayList();
        try {
            read =  FXCollections.observableList(quantifiersReader.read());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.newTabWithContent(Quantifier.class, "Kwantyfikatory", read);
    }

    public void showQualifiers() {
        ObservableList<Qualifier> read = FXCollections.observableArrayList();
        try {
            read =  FXCollections.observableList(qualifiersReader.read());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.newTabWithContent(Qualifier.class, "Kwalifikatory", read);
    }

    private <T> void newTabWithContent(Class<T> tClass, String name, List<T> content) {
        TableView tableView = new TableView();
        List<TableColumn<String, T>> simpleColumns =
                CommonFXUtils.getSimpleColumnsForClass(tClass, false);
        tableView.getColumns().addAll(simpleColumns);
        tableView.setItems(FXCollections.observableList(content));
        tab1.getTabPane().getTabs().add(new Tab(name, tableView));
    }
}

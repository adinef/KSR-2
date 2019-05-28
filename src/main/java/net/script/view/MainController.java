package net.script.view;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import net.script.Main;
import net.script.data.annotations.enums.Author;
import net.script.data.entities.DCResMeasurement;
import net.script.data.repositories.DCResMeasurementRepository;
import net.script.utils.CommonFXUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
@Slf4j
public class MainController implements Initializable {

    private boolean isFullscreen;
    private final DCResMeasurementRepository DCResMeasurementRepository;

    @FXML
    private JFXTreeTableView table;

    @Autowired
    public MainController(DCResMeasurementRepository DCResMeasurementRepository) {
        this.DCResMeasurementRepository = DCResMeasurementRepository;
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
    private void loadData() {
        List<JFXTreeTableColumn<DCResMeasurement, ?>> columnsForClass = CommonFXUtils.getColumnsForClass(
                DCResMeasurement.class,
                true
        );
        this.table.getColumns().addAll(columnsForClass);
    }
}

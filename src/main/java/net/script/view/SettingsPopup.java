package net.script.view;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.script.config.paths.PathConfig;
import net.script.config.paths.PathType;
import net.script.logic.settings.qualifier.QualifiersConfigAccessor;
import net.script.logic.settings.quantifier.QuantifiersConfigAccessor;
import net.script.logic.settings.summarizer.SummarizersConfigAccessor;
import net.script.utils.CommonFXUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class SettingsPopup {

    private final PathConfig pathConfig;
    private final QuantifiersConfigAccessor quantifiersConfigAccessor;
    private final QualifiersConfigAccessor qualifiersConfigAccessor;
    private final SummarizersConfigAccessor summarizersConfigAccessor;

    private FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Plik .xml", "xml");

    @Autowired
    public SettingsPopup(PathConfig pathConfig,
                         QuantifiersConfigAccessor quantifiersConfigAccessor,
                         QualifiersConfigAccessor qualifiersConfigAccessor,
                         SummarizersConfigAccessor summarizersConfigAccessor) {
        this.pathConfig = pathConfig;
        this.quantifiersConfigAccessor = quantifiersConfigAccessor;
        this.qualifiersConfigAccessor = qualifiersConfigAccessor;
        this.summarizersConfigAccessor = summarizersConfigAccessor;
    }

    public void show(Scene scene) {
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();

        JFXButton noButton = new JFXButton("Anuluj");
        noButton.setButtonType(JFXButton.ButtonType.FLAT);
        noButton.setOnAction(event -> alert.hideWithAnimation());

        JFXButton yesButton = new JFXButton("Zapisz");
        yesButton.setButtonType(JFXButton.ButtonType.FLAT);
        yesButton.setOnAction(event -> {
            alert.hideWithAnimation();
        });
        VBox mainVbox = new VBox();

        NodeData qfData = this.newBlockData("Ścieżka pliku konfiguracji kwalifikatorów",
                "Wybierz ścieżkę konfiguracji kwalifikatorów",
                scene,
                PathType.QUALIFIERS
        );

        NodeData qData = this.newBlockData("Ścieżka pliku konfiguracji kwantyfikatorów",
                "Wybierz ścieżkę konfiguracji kwantyfikatorów",
                scene,
                PathType.QUANTIFIERS
        );

        NodeData sData = this.newBlockData("Ścieżka pliku konfiguracji sumaryzatorów",
                "Wybierz ścieżkę konfiguracji sumaryzatorów",
                scene,
                PathType.SUMMARIZERS
        );


        yesButton.setOnAction(
                (e) -> {
                    String qfPath = this.pathConfig.knownPathFor(PathType.QUALIFIERS);
                    String qPath = this.pathConfig.knownPathFor(PathType.QUANTIFIERS);
                    String sPath = this.pathConfig.knownPathFor(PathType.SUMMARIZERS);
                    try {
                        this.pathConfig.setValue(PathType.QUALIFIERS, qfData.textField.getText());
                        this.pathConfig.setValue(PathType.QUANTIFIERS, qData.textField.getText());
                        this.pathConfig.setValue(PathType.SUMMARIZERS, sData.textField.getText());
                        this.qualifiersConfigAccessor.read(false);
                        this.quantifiersConfigAccessor.read(false);
                        this.summarizersConfigAccessor.read(false);
                        this.pathConfig.savePathSettings();
                        alert.hideWithAnimation();
                    } catch (Exception e1) {
                        this.pathConfig.setValue(PathType.QUALIFIERS, qfPath);
                        this.pathConfig.setValue(PathType.QUANTIFIERS, qPath);
                        this.pathConfig.setValue(PathType.SUMMARIZERS, sPath);
                        e1.printStackTrace();
                        CommonFXUtils.noDataPopup(
                                "Błąd",
                                "Nie udało się zapisać danych, pewnij się" +
                                        "że ustawienia ścieżek są poprawne.",
                                scene
                        );
                    }
                }
        );

        mainVbox.getChildren().addAll(qData.nodes);
        mainVbox.getChildren().addAll(qfData.nodes);
        mainVbox.getChildren().addAll(sData.nodes);

        layout.setHeading(new Label("Ustawienia"));
        layout.setBody(mainVbox);
        layout.setActions(noButton, yesButton);
        layout.setMaxWidth(500);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setWidth(600);
        alert.setOverlayClose(false);
        alert.setHideOnEscape(false);
        alert.setContent(layout);
        alert.showAndWait();
    }

    private NodeData newBlockData(String title, String fileDesc, Scene scene, PathType pathType) {
        ObservableList<Node> nodes = FXCollections.observableArrayList();
        Label pathLabel = new Label(title);
        HBox hBox = new HBox();
        TextField textBox = new TextField();
        textBox.setText(pathConfig.knownPathFor(pathType));
        textBox.setMinWidth(450);
        JFXButton selectButton = new JFXButton("Wybierz");
        hBox.getChildren().addAll(textBox, selectButton);
        selectButton.setOnAction(
                (e) -> {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(new File("./"));
                    fileChooser.setTitle(fileDesc);
                    fileChooser.setSelectedExtensionFilter(extensionFilter);
                    File file = fileChooser.showOpenDialog(scene.getWindow());
                    if (file != null) {
                        textBox.setText(file.getAbsolutePath());
                    }
                }
        );
        nodes.addAll(pathLabel, hBox);
        NodeData nodeData = new NodeData();
        nodeData.nodes = nodes;
        nodeData.textField = textBox;
        return nodeData;
    }

    private class NodeData {
        TextField textField;
        List<Node> nodes;
    }
}


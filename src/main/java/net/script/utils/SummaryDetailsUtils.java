package net.script.utils;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.script.data.annotations.Column;
import net.script.logic.summary.QualityTuple;
import net.script.view.Summary;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class SummaryDetailsUtils {
    public static void showDetailsOf(Summary summary, Scene scene) {
        ScrollPane insideScrollPane = new ScrollPane();

        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        alert.setWidth(700);
        JFXButton closeButton = new JFXButton();
        closeButton.setOnAction(
                (e) -> {
                    alert.hideWithAnimation();
                    e.consume();
                }
        );
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(20));
        vBox.setMinWidth(400);
        List<Node> labelAndData = getNameValues(summary);
        vBox.getChildren().addAll(labelAndData);
        insideScrollPane.setMinHeight(500);
        insideScrollPane.setMaxHeight(500);
        insideScrollPane.setContent(vBox);
        layout.setBody(insideScrollPane);
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.showAndWait();
    }

    private static List<Node> getNameValues(Summary summary) {
        List<Node> labelsAndValues = new ArrayList<>();
        for (Field field : summary.getClass().getDeclaredFields()) {
            Column colAnn = field.getAnnotation(Column.class);
            if (colAnn != null) {
                field.setAccessible(true);
                Label label = new Label(colAnn.value());
                Object fieldValue;
                try {
                    fieldValue = field.get(summary);
                    if (field.getType().equals(QualityTuple.class)) {
                        fieldValue = ((QualityTuple)fieldValue).getMetadata();
                    }
                } catch (IllegalAccessException e) {
                    fieldValue = "";
                    e.printStackTrace();
                }
                Separator separator = new Separator(Orientation.HORIZONTAL);
                TextField textField = new TextField();
                textField.setMaxWidth(200);
                textField.setEditable(false);
                textField.setText( fieldValue.toString() );
                labelsAndValues.add(label);
                labelsAndValues.add(textField);
                labelsAndValues.add(separator);
            }
        }
        return labelsAndValues;
    }
}

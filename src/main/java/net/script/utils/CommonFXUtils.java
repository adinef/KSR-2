package net.script.utils;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.script.Main;
import net.script.data.annotations.Column;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CommonFXUtils {
    public static Optional noDataPopup(String title, String body, Scene scene) {
        JFXAlert alert = new JFXAlert((Stage) scene.getWindow());
        JFXDialogLayout layout = new JFXDialogLayout();
        JFXButton closeButton = new JFXButton("Close");
        closeButton.setButtonType(JFXButton.ButtonType.FLAT);
        closeButton.setOnAction(event -> alert.hideWithAnimation());
        layout.setHeading(new Label(title));
        layout.setBody(new Label(body));
        layout.setActions(closeButton);
        alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        alert.setContent(layout);
        return alert.showAndWait();
    }

    public static  <T> List<TableColumn<String, T>> getSimpleColumnsForClass(Class<T> tClass, boolean editable) {
        Field[] allFields = tClass.getDeclaredFields();
        List<TableColumn<String, T>> columns = new LinkedList<>();
        for (Field field : allFields) {
            createColumn(editable, columns, field);
        }
        for (Field field : tClass.getSuperclass().getDeclaredFields()) {
            createColumn(editable, columns, field);
        }
        return columns;
    }

    private static <T> void createColumn(boolean editable, List<TableColumn<String, T>> columns, Field field) {
        Column annotation = field.getAnnotation(Column.class);
        if (annotation != null) {
            TableColumn<String, T> column = new TableColumn<>(annotation.value());
            column.setEditable(editable);
            column.setPrefWidth(75);
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
            columns.add(column);
        }
    }

    public static void longTaskWithMessages(RunnableWithException r, String onSuccess, String onFailure, Scene scene) {
        LongTask longTask = new LongTask(r);
        longTask.setOnSucceeded( (e) -> {
            if (e.getSource().getValue().equals(true)) {
                CommonFXUtils.noDataPopup("Sukces",
                        onSuccess,
                        Main.getCurrentStage().getScene()
                );
            } else {
                CommonFXUtils.noDataPopup("Porażka",
                        onFailure,
                        Main.getCurrentStage().getScene()
                );
            }
        });
        longTask.start();
    }
}

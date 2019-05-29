package net.script.utils;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.cells.editors.TextFieldEditorBuilder;
import com.jfoenix.controls.cells.editors.base.GenericEditableTreeTableCell;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import net.script.data.annotations.Column;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;

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

    public static <T> List<JFXTreeTableColumn<T, ?>> getColumnsForClass(
            Class<T> type,
            boolean editable) {

        Field[] allFields = type.getDeclaredFields();
        List<JFXTreeTableColumn<T, ?>> columns = new LinkedList<>();
        for (Field field : allFields) {
            Column annotation = field.getAnnotation(Column.class);
            if (annotation != null) {
                JFXTreeTableColumn<T, ?> column = new JFXTreeTableColumn<>(annotation.value());
                column.setEditable(editable);
                column.setCellFactory((param) -> new GenericEditableTreeTableCell<>(
                        new TextFieldEditorBuilder()));
                column.setCellValueFactory((elem) -> cvFactory(field, elem));
                column.setOnEditCommit(
                        cellEditEvent -> {
                            onEdit(cellEditEvent, (obj, val) -> onEditAction(obj, val, field));
                        }
                );
                columns.add(
                        column
                );
            }
        }
        return columns;
    }

    private static <T> void onEditAction(T obj, Object val, Field field) {
        field.setAccessible(true);
        try {
            field.set(obj, val);
        } catch (IllegalAccessException e) {
            log.error("Error during value set", e);
        }
    }

    private static <T> ObservableValue cvFactory(Field field, TreeTableColumn.CellDataFeatures<T, ?> elem) {
        T object = elem.getValue().getValue();
        field.setAccessible(true);
        Object o = null;
        try {
            o = field.get(object);
        } catch (IllegalAccessException e) {
            log.error("Error reading object value for cell factory", e);
        }
        if (o == null) {
            try {
                Constructor<?> constructor = field.getType().getConstructor();
                o = constructor.newInstance();
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        }
        if (field.getType().equals(Integer.class)) {
            return new SimpleIntegerProperty((Integer)o);
        } else if (field.getType().equals(String.class)) {
            return new SimpleStringProperty((String)o);
        } else if (field.getType().equals(Float.class)) {
            return new SimpleFloatProperty((Float)o);
        } else if (field.getType().equals(Double.class)) {
            return new SimpleDoubleProperty((Float)o);
        } else if (field.getType().equals(Boolean.class)) {
            return new SimpleBooleanProperty((Boolean) o);
        } else if (field.getType().equals(Long.class)) {
            return new SimpleLongProperty((Long) o);
        } else {
            return new SimpleStringProperty("");
        }
    }

    private static <T> void onEdit(TreeTableColumn.CellEditEvent<T, ?> cellEditEvent, BiConsumer<T, Object> cons) {
        T value = cellEditEvent
                .getTreeTableView()
                .getTreeItem(cellEditEvent
                        .getTreeTablePosition()
                        .getRow()
                ).getValue();
        cons.accept(value, cellEditEvent.getNewValue());
    }
}

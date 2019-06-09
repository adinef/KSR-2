package net.script.utils;

import javafx.stage.FileChooser;
import net.script.Main;
import net.script.data.annotations.Column;
import net.script.logic.quantifier.Quantifier;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;

public class CSVSaveUtils {

    public static void exportToCsv(List<?> lvs, String separator, Class<?> clazz) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./"));
        fileChooser.setTitle("Wybierz katalog");
        File file = fileChooser.showSaveDialog(Main.getCurrentStage());
        if (file != null) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    CommonFXUtils.noDataPopup(
                            "Błąd",
                            "Wystąpił błąd w trakcie zapisu. " + e.getMessage(),
                            Main.getCurrentStage().getScene()
                    );
                    e.printStackTrace();
                    return;
                }
            }
            String data = "";
            Field[] declaredFields = clazz.getDeclaredFields();

            for (int i = 0; i < declaredFields.length; i++) {
                Column annotation = declaredFields[i].getAnnotation(Column.class);
                data = getHeadersExtracted(data, declaredFields, i, annotation, separator);
            }
            Class lClass = clazz;
            while (lClass != null) {
                for (int i = 0; i < lClass.getDeclaredFields().length; i++) {
                    Column annotation = lClass.getDeclaredFields()[i].getAnnotation(Column.class);
                    data = getHeadersExtracted(data, lClass.getDeclaredFields(), i, annotation, separator);
                }
                lClass = lClass.getSuperclass();
            }
            for (Object elem : lvs) {
                data = getValuesExtracted(data, declaredFields, elem, separator);
                lClass = clazz.getSuperclass();
                while (lClass != null) {
                    data = getValuesExtracted(data, lClass.getDeclaredFields(), elem, separator);
                    lClass = lClass.getSuperclass();
                }
            }

            try {
                Files.write(file.toPath(), data.getBytes());
            } catch (IOException e) {
                CommonFXUtils.noDataPopup(
                        "Błąd",
                        "Wystąpił błąd w trakcie zapisu. " + e.getMessage(),
                        Main.getCurrentStage().getScene()
                );
                e.printStackTrace();
            }
        }
    }

    private static String getHeadersExtracted(String data, Field[] declaredFields, int i, Column annotation, String separator) {
        if (annotation != null) {
            data += annotation.value();
            if (i < declaredFields.length - 1) {
                data += " " + separator + " ";
            } else {
                data += "\n";
            }
        }
        return data;
    }

    private static String getValuesExtracted(String data, Field[] declaredFields, Object elem, String separator) {
        for (int i = 0; i < declaredFields.length; i++) {
            Column annotation = declaredFields[i].getAnnotation(Column.class);
            if (annotation != null) {
                Object obj;
                declaredFields[i].setAccessible(true);
                try {
                    obj = declaredFields[i].get(elem);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    obj = null;
                }
                data += obj;
                if (i < declaredFields.length - 1) {
                    data += " " + separator + " ";
                } else {
                    data += "\n";
                }
            }
        }
        return data;
    }

    public static void exportQuantifiersToCsv(List<Quantifier> qs) {

    }
}

package net.script;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.script.config.main.ApplicationVariables;
import net.script.data.csv.CsvReader;
import net.script.data.entities.DCResMeasurement;
import net.script.data.repositories.CachingRepository;
import net.script.logic.fuzzy.FuzzySet;
import net.script.logic.quantifier.Quantifier;
import net.script.logic.settings.Reader;
import net.script.logic.settings.quantifier.QuantifiersReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class Main extends Application {

    private ConfigurableApplicationContext springContext;
    private FXMLLoader fxmlLoader;
    private double xOffset = 0;
    private double yOffset = 0;

    private static Stage currentStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        fxmlLoader.setLocation(getClass().getResource("/fxml/sample.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Hello World");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        //MOVE IT TO SCENE CONTROLLER
        root.setOnMousePressed( (e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        root.setOnMouseDragged((e) -> {
            primaryStage.setX(e.getScreenX() - xOffset);
            primaryStage.setY(e.getScreenY() - yOffset);
        });
        primaryStage.setScene(new Scene(root, 1055, 705));
        primaryStage.show();
        currentStage = primaryStage;
    }

    public static Stage getCurrentStage() {
        return currentStage;
    }

    public static void main(String[] args) {
        if (args != null) {
            ApplicationVariables.INIT_VALUES.addAll(Arrays.asList(args));
        }
        launch(args);
    }

    @Override
    public void init() {
        springContext = SpringApplication.run(Main.class);
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(springContext::getBean);
    }

    @Override
    public void stop() {
        springContext.stop();
    }
}

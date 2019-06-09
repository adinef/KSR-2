package net.script;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.script.config.main.ApplicationVariables;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

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
        primaryStage.setTitle("KSR 2 - Podsumowania lingwistyczne.");
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
        Scene scene = new Scene(root, 1200, 900);
        scene.getStylesheets().add( getClass().getResource("/css/style.css").toExternalForm() );
        primaryStage.setScene(scene);
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

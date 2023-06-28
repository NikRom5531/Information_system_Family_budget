package com.example.kursproject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
public class ControlStages {
    private static Stage stage;
    public static String filePathURL;
    public static Stage getStage(){
        return stage;
    }
    public ControlStages(Stage stage) {
        setAppIcon(stage);
        stage.setResizable(false);
        this.stage = stage;
    }

    public ControlStages() {
    }
    public static void changeScene(String nameScene) throws IOException {
        filePathURL = nameScene; System.out.println(filePathURL);
        Parent root = FXMLLoader.load(Objects.requireNonNull(Application.class.getResource(nameScene)));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void setAppIcon(Stage stage1){
        stage1.getIcons().add(new Image(Objects.requireNonNull(Application.class.getResourceAsStream("images/AppIcon.png"))));
    }
    public static String getSceneURL(){
        return filePathURL;
    }
    //////////////////////////////////////////////////
    private static Stage secondStage;

    public static void setSecondStage(Stage stage){
        secondStage = stage;
    }

    public static void closeSecondStage(){
            secondStage.close();
            secondStage = null;
    }
    //////////////////////////////////////////////////
    public static void newSecondStage(String title, String nameScene){
        Stage newStage = new Stage();
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(ControlStages.class.getResource(nameScene)));
            Scene newScene = new Scene(root);
            newStage.setScene(newScene);
            newStage.initModality(Modality.WINDOW_MODAL);
            newStage.initOwner(getStage());
            newStage.setTitle(title);
            newStage.setOnHidden(Event -> closeSecondStage());
            newStage.setResizable(false);
            newStage.show();
            // Дополнительные настройки для newStage
            setAppIcon(newStage);
            setSecondStage(newStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

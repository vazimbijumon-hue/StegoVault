package stegovault;
import stegovault.core.KeyManager;
import stegovault.core.KeyMaterial;

import javafx.scene.image.Image;
import javax.crypto.SecretKey;


import stegovault.core.SessionManager;
import stegovault.core.StegoVaultService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import stegovault.ui.MainWindow;



public class Main extends Application {

  
    @Override
    public void start(Stage stage) {

           SecretKey sessionKey =
        KeyManager.generateSessionKey();

SessionManager sessionManager =
        new SessionManager(sessionKey);

StegoVaultService stegoVaultService =
        new StegoVaultService(sessionManager);

Scene scene =
        new MainWindow(stegoVaultService).createScene();

stage.getIcons().add(
    new Image(getClass().getResourceAsStream("/images/icon.png"))

);
stage.setTitle("StegoVault");


stage.setScene(scene);

stage.setMinWidth(1000);
stage.setMinHeight(650);

stage.setWidth(1200);
stage.setHeight(750);

stage.centerOnScreen();

stage.show();
      
    }


    public static void main(String[] args) {


        launch();
    }
}
package com.sqlibri;

import java.net.URL;

import com.sqlibri.presenter.AppPersenter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main class - launches the application
 */
public class App extends Application {

  // Application Icon
  private final Image ROOT_ICON = new Image(
      getClass().getResource("resources/image/SQLibri-icon.png").toExternalForm());
  
  // Application title
  private final String TITLE = "SQLibri";
  
  // Main Layout
  private final URL MAIN_LAYOUT = getClass()
      .getResource("resources/layout/main-layout.fxml");
  
  @Override
  public void start(Stage primaryStage) {
    try {
      FXMLLoader loader = new FXMLLoader(MAIN_LAYOUT);
      Parent root = loader.load();

      Scene scene = new Scene(root);
      primaryStage.setScene(scene);

      AppPersenter controller = (AppPersenter) loader.getController();
      controller.init(primaryStage);

      primaryStage.setTitle(TITLE);
      primaryStage.getIcons().add(ROOT_ICON);
      primaryStage.setMaximized(true);
      primaryStage.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}

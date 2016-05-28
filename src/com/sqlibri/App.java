package com.sqlibri;

import com.sqlibri.presenter.AppPersenter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Main class - launches the application
 */
public class App extends Application {

	// Application Icon
	private final Image rootIcon = new Image(getClass()
			.getResource("resources/image/sqlibri.png").toExternalForm());
	
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("resources/layout/main-layout.fxml"));
			BorderPane root = (BorderPane) loader.load();
			
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			
			AppPersenter controller = (AppPersenter) loader.getController();
			controller.init(primaryStage);
			
			primaryStage.setTitle("sqlibri");
			primaryStage.getIcons().add(rootIcon);
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

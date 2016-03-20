package com.sqlibri;

import com.sqlibri.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

	// Application Icon
	private final Image rootIcon = new Image("com/sqlibri/resources/image/sqlibri.png");
	

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass()
					.getResource("resources/layout/main-layout.fxml"));
			BorderPane root = (BorderPane) loader.load();
			MainController controller = (MainController) loader.getController();
			controller.setStage(primaryStage);
			
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("sqlibri");
			primaryStage.getIcons().add(rootIcon);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}

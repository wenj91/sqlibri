package com.sqlibri.presenter;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.sqlibri.App;
import com.sqlibri.model.Database;
import com.sqlibri.model.QueryResult;
import com.sqlibri.util.CSVParser;
import com.sqlibri.util.PrettyStatus;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AppPersenter implements Initializable {

	private Stage window;

	private Database db;

	@FXML
	private TreeView<String> dbStructure;
	private final String DB_ICON = "com/sqlibri/resources/image/database.png";
	private final String TABLE_ICON = "com/sqlibri/resources/image/table.png";

	@FXML
	private TextArea SQLEditor;

	@FXML
	private TableView<ObservableList<String>> table;

	@FXML
	private Label statusBar;

	@FXML
	private Button execute;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		SQLEditor.setOnKeyPressed(e -> {
			if (e.getCode() == KeyCode.F9)
				execute();
		});
	}

	public void setStage(Stage primaryStage) {
		window = primaryStage;
	}

	private void showErrorDialog(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void loadTables(File database) {
		Image rootImg = new Image(DB_ICON, 16, 16, false, false);
		TreeItem<String> rootItem = new TreeItem<String>(database.getName(), new ImageView(rootImg));
		rootItem.setExpanded(true);

		Image tableImg = new Image(TABLE_ICON, 16, 16, false, false);

		List<String> tables = new Database(database).getAllTables();

		List<TreeItem<String>> tablesList = new ArrayList<>();
		for (String table : tables) {
			tablesList.add(new TreeItem<String>(table, new ImageView(tableImg)));
		}

		rootItem.getChildren().addAll(tablesList);
		dbStructure.setRoot(rootItem);
	}

	private void clearTables() {
		dbStructure.setRoot(null);
	}

	private void loadTableView(QueryResult queryResult) {
		table.getColumns().clear();
		table.getItems().clear();

		if (queryResult.getTableData().size() <= 0)
			return;

		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
		for (int column = 0; column < queryResult.getColumnCount(); column++) {
			final int j = column;

			TableColumn<ObservableList<String>, String> col = new TableColumn<>(queryResult.getColumnNames().get(j));

			col.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(j).toString()));

			table.getColumns().addAll(col);
		}

		for (int row = 0; row < queryResult.getRowCount(); row++) {
			ObservableList<String> rowData = FXCollections.observableArrayList();
			for (int column = 0; column < queryResult.getColumnCount(); column++) {
				rowData.add(queryResult.getTableData().get(row).get(column).toString());
			}
			data.add(rowData);
		}

		table.setItems(data);

	}

	@FXML
	public void createDb() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Create Database File");
		File file = fileChooser.showSaveDialog(window);

		if (file == null)
			return;

		db = new Database(file);

		try {
			db.connect();
		} catch (SQLException e) {
			showErrorDialog("ERROR", "SQL ERROR:", e.getMessage());
		} catch (Exception e) {
			showErrorDialog("ERROR", "Unexpected ERROR:", e.getMessage());
		}

		loadTables(db.getFile());
	}

	@FXML
	public void dropDb() {
		if (db == null || db.getFile() == null)
			return;

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("DROP DATABASE");
		alert.setHeaderText("Are you really want to drop the database?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			db.drop();
			clearTables();
		}

	}

	@FXML
	public void openDb() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Database File");
		File file = fileChooser.showOpenDialog(window);

		if (file == null)
			return;

		if (file != null) {
			db = new Database(file);
			loadTables(db.getFile());
		}
	}

	@FXML
	public void copyDb() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Database File");
		File file = fileChooser.showSaveDialog(window);

		if (file == null)
			return;

		try {
			Files.copy(db.getFile().toPath(), file.toPath());
		} catch (IOException e) {
			showErrorDialog("ERROR", "FILE IO ERROR:", e.getMessage());
		} catch (Exception e) {
			showErrorDialog("ERROR", "Unexpected ERROR:", e.getMessage());
		}
	}

	@FXML
	public void saveQuery() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Query");
		File file = fileChooser.showSaveDialog(window);

		if (file == null)
			return;

		try {
			Files.write(file.toPath(), SQLEditor.getText().getBytes());
		} catch (IOException e) {
			showErrorDialog("ERROR", "FILE IO ERROR:", e.getMessage());
		} catch (Exception e) {
			showErrorDialog("ERROR", "Unexpected ERROR:", e.getMessage());
		}

	}

	@FXML
	public void loadQuery() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Query File");
		File file = fileChooser.showOpenDialog(window);

		if (file == null)
			return;

		String query = "";

		try {
			query = Files.readAllLines(file.toPath()).stream().collect(Collectors.joining());
		} catch (IOException e) {
			showErrorDialog("ERROR", "FILE IO ERROR:", e.getMessage());
		} catch (Exception e) {
			showErrorDialog("ERROR", "Unexpected ERROR:", e.getMessage());
		}

		SQLEditor.setText(query);
	}

	@FXML
	public void exportCSV() {
		/*
		 * TODO Implement parsing QueryResult or TableView Observable List to
		 * CSV format
		 */

		CSVParser csvParser = new CSVParser();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export to CSV");
		File file = fileChooser.showOpenDialog(window);

		if (file == null)
			return;

		try {
			Files.write(file.toPath(), "AAA".getBytes());
		} catch (IOException e) {
			showErrorDialog("ERROR", "FILE IO ERROR:", e.getMessage());
		} catch (Exception e) {
			showErrorDialog("ERROR", "Unexpected ERROR:", e.getMessage());
		}

	}

	@FXML
	public void exit() {
		Platform.exit();
	}

	@FXML
	public void execute() {
		if (db == null || db.getFile() == null)
			return;

		QueryResult queryResult = null;
		String query = SQLEditor.getText();
		try {
			queryResult = db.executeQuery(query);
		} catch (SQLException e) {
			showErrorDialog("ERROR", "SQL ERROR:", e.getMessage());
			statusBar.setText(PrettyStatus.error(query));
		} catch (Exception e) {
			showErrorDialog("ERROR", "Unexpected ERROR:", e.getMessage());
		}

		if (queryResult != null) {
			loadTables(db.getFile());
			if (queryResult.getTableData() != null) {
				loadTableView(queryResult);
				queryResult.getTableData().forEach(System.out::println);
			}
			if (queryResult.getExecutionInfo() != null) {
				statusBar.setText(queryResult.getExecutionInfo());
			}
		}

	}

	@FXML
	public void showUserGuide() {
		Stage userGuide = new Stage();
		Accordion accordion = null;
		try {
			accordion = FXMLLoader.load(App.class.getResource("resources/layout/user-guide.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		userGuide.setScene(new Scene(accordion));
		userGuide.setTitle("User Guide");
		userGuide.show();
	}

	@FXML
	public void showAbout() {
		Stage about = new Stage();
		AnchorPane aboutPane = null;
		try {
			aboutPane = FXMLLoader.load(App.class.getResource("resources/layout/about.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		about.setScene(new Scene(aboutPane));
		about.setTitle("About");
		about.setResizable(false);

		about.show();
	}

}

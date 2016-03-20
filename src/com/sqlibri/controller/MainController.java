package com.sqlibri.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.sqlibri.model.Database;
import com.sqlibri.model.QueryResult;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {

	private Stage window;

	private Database db;

	@FXML
	private TreeView<String> dbStructure;
	private final String dbIcon = "com/sqlibri/resources/image/database.png";
	private final String tableIcon = "com/sqlibri/resources/image/table.png";

	@FXML
	private TextArea SQLEditor;

	@FXML
	private TableView table;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void setStage(Stage primaryStage) {
		window = primaryStage;
	}

	private void loadTables(File database) {
		Image rootImg = new Image(dbIcon, 16, 16, false, false);
		TreeItem<String> rootItem = new TreeItem<String>(database.getName(), new ImageView(rootImg));
		rootItem.setExpanded(true);

		Image tableImg = new Image(tableIcon, 16, 16, false, false);

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

	private void loadTableView(QueryResult tableData) {
		/*
		 * TODO Implement parsing QueryResult to TableView. Shit below doesn't
		 * works. (((
		 */
		table.setEditable(true);
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
		for (int i = 0; i < tableData.getColumnNames().size(); i++) {
			TableColumn col = new TableColumn(tableData.getColumnNames().get(i));
			col.setCellValueFactory(param -> new SimpleStringProperty(param.toString()));
			table.getColumns().addAll(col);
		}

		for (int row = 0; row < tableData.getTableData().size(); row++) {
			ObservableList<String> rowData = FXCollections.observableArrayList();
			for (int column = 0; column < tableData.getTableData().get(row).size(); column++) {
				rowData.add(tableData.getTableData().get(row).get(column).toString());
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

		db = new Database(file);

		try {
			db.connect();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		loadTables(db.getFile());

	}

	@FXML
	public void dropDb() {
		db.drop();
		clearTables();
	}

	@FXML
	public void openDb() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Database File");
		File file = fileChooser.showOpenDialog(window);
		db = new Database(file);
		loadTables(db.getFile());
	}

	@FXML
	public void copyDb() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Database File");
		File file = fileChooser.showSaveDialog(window);

		try {
			Files.copy(db.getFile().toPath(), file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void saveQuery() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Query");
		File file = fileChooser.showSaveDialog(window);
		try {
			Files.write(file.toPath(), SQLEditor.getText().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void loadQuery() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Query File");
		File file = fileChooser.showOpenDialog(window);

		String query = "";

		try {
			query = Files.readAllLines(file.toPath()).stream().collect(Collectors.joining());
		} catch (IOException e) {
			e.printStackTrace();
		}

		SQLEditor.setText(query);
	}

	@FXML
	public void exportCSV() {
		/*
		 * TODO Implement parsing QueryResult or TableView Observable List to
		 * CSV format
		 */
	}

	@FXML
	public void exit() {
		Platform.exit();
	}

	@FXML
	public void execute() {
		QueryResult qr = db.executeQuery(SQLEditor.getText());
		if (qr.getTableData() != null)
			qr.getTableData().forEach(System.out::println);
		loadTables(db.getFile());
		loadTableView(qr);
	}

}

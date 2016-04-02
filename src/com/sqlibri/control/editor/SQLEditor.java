package com.sqlibri.control.editor;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class SQLEditor extends StackPane {

	@FXML
	private WebView editor;

	private WebEngine engine;

	public SQLEditor() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SQLEditor.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		engine = editor.getEngine();

		engine.load(getClass().getResource("editor.html").toExternalForm());
		
		editor.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
	        if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.V){
	            final Clipboard clipboard = Clipboard.getSystemClipboard();
	            String content = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
	            content = content.replaceAll("(\n|\r)", " ");
	            engine.executeScript(" pasteContent(\""+content+"\") ");

	        }
	    });

	}

	public String getCode() {
		return (String) engine.executeScript("editor.getValue()");
	}

	public void pasteCode(String code) {
		engine.executeScript("editor.setValue(\"" + code + "\");");
	}

}

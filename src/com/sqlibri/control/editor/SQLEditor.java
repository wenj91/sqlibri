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

/**
 * JavaFX control which implements SQL code editor
 * using WebView with ace.js for syntax highlighting and intelliSence 
 */
public class SQLEditor extends StackPane {

	// WebView with ace.js
	@FXML private WebView editor;

	private WebEngine engine;

	/**
	 * Initialize Layout with FXML and loads ace.js to the webView
	 */
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
		
		//Add hook solution to add Ctrl+V to the Javafx webView
		editor.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
	        if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.V){
	            final Clipboard clipboard = Clipboard.getSystemClipboard();
	            String content = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);
	            content = content.replaceAll("(\n|\r)", " ");
	            engine.executeScript(" pasteContent(\""+content+"\") ");

	        }
	    });

	}

	/**
	 * @return code from sql editor
	 */
	public String getCode() {
		return (String) engine.executeScript("editor.getValue()");
	}

	/**
	 * Puts Code to the sql editor
	 * @param code to paste to the sql editor
	 */
	public void pasteCode(String code) {
		engine.executeScript("editor.setValue(\"" + code + "\");");
	}

}

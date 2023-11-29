package gui.utils;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javafx.scene.control.Alert.AlertType;

public class FileUtils {
	
	//Return file from the specified path
	public static File getFile(String path) {
		File file = new File(path);
		return file;
	}

	//Loading an existing document
	public static PDDocument loadDocument(File file) {
		PDDocument document = null;

		try {
			document = Loader.loadPDF(file);
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error loading document", e.getMessage(), AlertType.ERROR);
		}
		return document;
	}

	//Instantiate PDFTextStripper class and retrieving text from PDF document
	public static String getTextFromPDF(PDDocument document) {
		String text = null;
		PDFTextStripper pdfStripper = new PDFTextStripper();

		try {
			text = pdfStripper.getText(document);
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error getting text", e.getMessage(), AlertType.ERROR);
		}
		return text;
	}

	//Closing the document
	public static void closeDocument(PDDocument document) {
		try {
			document.close();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Error closing document", e.getMessage(), AlertType.ERROR);
		}
	}
}

package gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;

import gui.utils.Alerts;
import gui.utils.FileUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainScreenController {

	@FXML
	public Button fileBrowser;
	@FXML
	public void fileBrowserTooltip() {
		fileBrowser.setTooltip(new Tooltip("Select the desired file"));
	}

	@FXML
	public Label fileName;
	@FXML
	public void fileNameTooltip() {
		fileName.setTooltip(new Tooltip("The file's name"));
	}

	@FXML
	public Button play;
	@FXML
	public void playTooltip() {
		play.setTooltip(new Tooltip("Play"));
	}

	@FXML
	public Button stop;
	@FXML
	public void stopTooltip() {
		stop.setTooltip(new Tooltip("Stop"));
	}

	@FXML
	public Button aWordBefore;
	@FXML
	public void aWordBeforeTooltip() {
		aWordBefore.setTooltip(new Tooltip("Go back one word"));
	}

	@FXML
	public Button fiveWordsBefore;
	@FXML
	public void fiveWordsBeforeTooltip() {
		fiveWordsBefore.setTooltip(new Tooltip("Go back five words"));
	}

	@FXML
	public Button decreaseDelay;
	@FXML
	public void decreaseDelayTooltip() {
		decreaseDelay.setTooltip(new Tooltip("Decrease delay"));
	}

	@FXML
	public Button increaseDelay;
	@FXML
	public void increaseDelayTooltip() {
		increaseDelay.setTooltip(new Tooltip("Increase delay"));
	}

	@FXML
	public Button pauseAndResume;
	@FXML
	public void pauseAndResumeTooltip() {
		pauseAndResume.setTooltip(new Tooltip("Pause and resume"));
	}

	@FXML
	public Label currentDelay;
	@FXML
	public void currentSpeedTooltip() {
		currentDelay.setTooltip(new Tooltip("Delay between words in seconds"));
	}
	
	@FXML
	public Label currentWordCount;
	
	@FXML
	public Label maxWordsCount;
	
	@FXML
	public Label isPausedOrNot;
	
	@FXML
	public Label currentWord;
	
	@FXML
	public Button about;
	
	String filePath = null;
	static double wordDelay = 0.75;
	static boolean isPaused = false;
	static boolean stopRequest = false;
	static Integer currentWordIndex = 0;
	ArrayList<String> words = new ArrayList<>();
	
	public boolean fileBrowser() {
		try {
			JFileChooser fileChooser= new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF", "pdf");
			fileChooser.setFileFilter(filter);
			fileChooser.setCurrentDirectory(new File("."));
			int result = fileChooser.showOpenDialog(null);
			System.out.println("Result" + result);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedFile = new File(fileChooser.getSelectedFile().getAbsolutePath());
				filePath = selectedFile.getAbsolutePath();
			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return false;
	}
	
	public void showFileName() {
		if (filePath != null) {
			File file = new File(filePath);
			fileName.setText(file.getName());
		}
	}

	@FXML
	public void onFileBrowserAction() {
		fileBrowser();
		showFileName();
		if (filePath == null) {
			return;
		}
		words = stringToArrayList(separateWordsByWhitespaceFromFile(filePath));
		maxWordsCount.setText("/" + words.size());
	}
	

	public String[] separateWordsByWhitespaceFromFile(String filePath) {
		PDDocument document = FileUtils.loadDocument(FileUtils.getFile(filePath));
		String textFromFile = FileUtils.getTextFromPDF(document);
		textFromFile = textFromFile.replace("\r\n", "");
		String[] wordsSeparatedByWhitespace = textFromFile.split(" ");
		FileUtils.closeDocument(document);
		return wordsSeparatedByWhitespace;
	}

	public ArrayList<String> stringToArrayList(String[] str) {
		ArrayList<String> words = new ArrayList<>();
		for (String e : str) {
			if (e != "") {
				words.add(e);
			}
		}
		return words;
	}

	@FXML
	public void onDecreaseDelayAction() {
		if (wordDelay == 1.0) {
			wordDelay = 0.75;
			currentDelay.setText("0.75s");
		} else if (wordDelay == 0.75) {
			wordDelay = 0.50;
			currentDelay.setText("0.50s");
		}
	}

	@FXML
	public void onIncreaseDelayAction() {
		if (wordDelay == 0.50) {
			wordDelay = 0.75;
			currentDelay.setText("0.75s");
		} else if (wordDelay == 0.75) {
			wordDelay = 1.0;
			currentDelay.setText("1.0s");
		}
	}
	
	public static void labelUpdaterWithDelay(ArrayList<String> list, int indexOfWord, Label labelForShowingWord, Label labelForCountingWord, double delay) {
		if (isPaused == true) {
			return;
		} else if (indexOfWord >= list.size() || stopRequest == true) {
			labelForCountingWord.setText("0");
			labelForShowingWord.setText(null);
			stopRequest = false;
			return;
		}
		double newDelay = wordDelay;
		Integer currentWordNumber = indexOfWord + 1;
		Timeline timeline = new Timeline(
		        new KeyFrame(
		            Duration.seconds(delay), 
		            e ->  currentWordIndex = indexOfWord),
		        new KeyFrame(
		        	Duration.seconds(delay),
		        	e -> {
		        		labelForShowingWord.setText(list.get(indexOfWord));
		        		labelForCountingWord.setText(currentWordNumber.toString());
		        		labelUpdaterWithDelay(list, indexOfWord+1, labelForShowingWord, labelForCountingWord, newDelay);
		        	}));
		timeline.playFromStart();
	}
	
	@FXML
	public void onAWordBeforeAction() {
		if ((words == null || words.isEmpty()) || currentWordIndex == 0) {
			return;
		}
		if (isPaused == true) {
			currentWordIndex -= 1;
			Integer currentWordNumber = currentWordIndex + 1;
			currentWordCount.setText(currentWordNumber.toString());
			currentWord.setText(words.get(currentWordIndex));
		} else {
			onPauseAndResumeAction();
			return;
		}
	}
	
	@FXML
	public void onFiveWordBeforeAction() {
		if ((words == null || words.isEmpty()) || currentWordIndex == 0) {
			return;
		}
		if (isPaused == true) {
			if (currentWordIndex < 5) {
				currentWordIndex = 0;
			} else {
				currentWordIndex -= 5;
			}
			Integer currentWordNumber = currentWordIndex + 1;
			currentWordCount.setText(currentWordNumber.toString());
			currentWord.setText(words.get(currentWordIndex));
		} else {
			onPauseAndResumeAction();
			return;
		}
	}
	
	@FXML
	public void onPauseAndResumeAction() {
		if (words == null && isPaused) {
			isPaused = false;
			isPausedOrNot.setText("");
			pauseAndResume.setText("\u23F8");
			return;
		} else if (words == null) {
			return;
		}
		if (words.isEmpty() && isPaused) {
			isPaused = false;
			isPausedOrNot.setText("");
			pauseAndResume.setText("\u23F8");
			return;
		} else if (words.isEmpty()) {
			return;
		}
		if (isPaused) {
			isPaused = false;
			isPausedOrNot.setText("");
			pauseAndResume.setText("\u23F8");
			labelUpdaterWithDelay(words, currentWordIndex, currentWord, currentWordCount, wordDelay);
		} else {
			isPaused = true;
			isPausedOrNot.setText("Paused");
			pauseAndResume.setText("\u23F5");
		}
	}

	@FXML
	public void onPlayAction() {
		if (filePath == null) {
			Alerts.showAlert("ERROR", "No file detected", null, AlertType.ERROR);
			return;
		}
		labelUpdaterWithDelay(words, 0, currentWord, currentWordCount, wordDelay);
	}

	@FXML
	public void onStopAction() {
		if (filePath == null) {
			return;
		}
		words = null;
		filePath = null;
		stopRequest = true;
		fileName.setText(null);
		currentWord.setText(null);
		maxWordsCount.setText("/0");
		currentWordCount.setText("0");
		onPauseAndResumeAction();
	}
	
	private void loadViewWithoutIcon(String absoluteName, String title) {
		try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            VBox root = loader.load();
            
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.initStyle(StageStyle.UTILITY); //only the close button remains
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	@FXML
	public void onAboutAction() {
		loadViewWithoutIcon("/gui/About.fxml", "About");
	}
}
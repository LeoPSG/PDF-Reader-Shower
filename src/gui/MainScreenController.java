package gui;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;

import gui.utils.Alerts;
import gui.utils.Constraints;
import gui.utils.FileUtils;
import gui.utils.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
		fileName.setTooltip(new Tooltip("File name"));
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
	public TextField numberOfWordToBeginWith;
	public void numberOfWordToBeginWithTooltip() {
		numberOfWordToBeginWith.setTooltip(new Tooltip("Insert the number of word to begin"));
	}
	
	@FXML
	public Label currentWordCount;
	
	@FXML
	public Label maxWordsCount;
	
	@FXML
	public Label isPausedOrNot;
	
	@FXML
	public Label currentWord;
	
	String filePath = null;
	static double wordDelay = 2.0;
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
		initializeNodes();
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
		if (wordDelay == 3.0) {
			wordDelay = 2.0;
			currentDelay.setText("2s");
		} else if (wordDelay == 2.0) {
			wordDelay = 1.0;
			currentDelay.setText("1s");
		}
	}

	@FXML
	public void onIncreaseDelayAction() {
		if (wordDelay == 1.0) {
			wordDelay = 2.0;
			currentDelay.setText("2s");
		} else if (wordDelay == 2.0) {
			wordDelay = 3.0;
			currentDelay.setText("3s");
		}
	}
	
	public static void labelUpdaterWithDelay(ArrayList<String> list, int indexOfWord, Label labelForShowingWord, Label labelForCountingWord, double delay) {
		if (isPaused == true) {
			return;
		} else if (indexOfWord >= list.size() || stopRequest == true) {
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
		if (words.isEmpty() || currentWordIndex == 0) {
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
		if (words.isEmpty() || currentWordIndex == 0) {
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
		if (words.isEmpty()) {
			return;
		}
		if (isPaused == true) {
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
		if (numberOfWordToBeginWith.getText() == "") {
			isPaused = true;
			isPausedOrNot.setText("Paused");
			pauseAndResume.setText("\u23F5");
			Alerts.showAlert("ERROR", "Number of word to begin is empty", null, AlertType.ERROR);
		} else if (numberOfWordToBeginWith.getText() == "1") {
			labelUpdaterWithDelay(words, 0, currentWord, currentWordCount, wordDelay);
		} else {
			int index = Utils.tryParseToInteger(numberOfWordToBeginWith.getText()) - 1;
			labelUpdaterWithDelay(words, index, currentWord, currentWordCount, wordDelay);
		}
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
		numberOfWordToBeginWith.setText("1");
	}
	
	private void initializeNodes() {
		if (numberOfWordToBeginWith.isEditable()) {
			return;
		}
		numberOfWordToBeginWith.setEditable(true);
		Constraints.setTextFieldInteger(numberOfWordToBeginWith);
		Constraints.setTextFieldMaxLength(numberOfWordToBeginWith, 6);
	}
}

package gui.utils;

import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Utils {
	
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}
	
	public static Double tryParseToDouble(String str) {
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static Integer tryParseToInteger(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static void removeExcessiveWhitespaceFromList(ArrayList<String> list) {
		for (String e : list) {
			if (!e.contains("\\W")) {
				list.remove(e);
			}
		}
	}
	
}

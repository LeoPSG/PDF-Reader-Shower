package gui.utils;

import java.util.ArrayList;

public class Utils {
	
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

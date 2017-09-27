package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyStringUtil {

	public static final String rusLettersString = "АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщъЫыьЭэЮюЯяЬ";
	public static final String actionPattern = "[()\"']?\\$\\$[Aa][Cc][Tt][_a-zA-Z0-9]*?\\$\\$[()\"']?";
	public static final String colorPattern = "%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\]";

	public static void main(String[] args) {
		System.out.println(hasLetterOrNumber(" Stamina regeneration:    "));
	}

	public static boolean isSentence(String str) {
		if (str.matches("[\\s\\S]*[" + rusLettersString + "][\\s\\S]*")) {
			return true;
		}
		if (str.contains("_")) {
			return false;
		}
		if (str.matches("[\\\\n\\s]*?")) {
			return false;
		}
		if (str.matches("[a-zA-Z0-9_]+?")) {
			return false;
		}
		if (str.contains("\\\\")) {
			return false;
		}
		return true;
		// not empty or literal empty string, not id either.
	}

	public static boolean hasLetterOrNumber(String str) {
		str = str.replaceAll("[\\s]", "");
		return str.matches("[\\S]*?[a-zA-Z0-9" + rusLettersString + "][\\S]*?[^:.]");
	}

	public static String cutString(String str, int pieceLength) {
		str = str.replaceAll(Pattern.quote("\n"), "");

		Pattern p1 = Pattern.compile(
				"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");
		Matcher m1 = p1.matcher(str);
		LinkedList<String> colorOrAction = new LinkedList<>();
		while (m1.find()) {
			colorOrAction.add(m1.group(0));
		}
		String[] pieces = str
				.split("(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");

		List<String> pieceList = new ArrayList<>(pieces.length);
		for (int i = 0; i < pieces.length; i++) {
			StringBuilder builder = new StringBuilder(pieces[i]);
			for (int j = 0; j < pieces[i].length() / pieceLength; j++) {
				builder.insert((j + 1) * pieceLength + j, "\n");
			}
			pieceList.add(builder.toString());
		}

		String result = "";

		for (int lengthMold = 0; !pieceList.isEmpty(); lengthMold = lengthMold % pieceLength) {
			if (lengthMold + pieceList.get(0).length() > pieceLength) {
				result = result + "\n" + pieceList.get(0);
				lengthMold = lengthMold + 1 + pieceList.get(0).length();
				lengthMold = lengthMold % pieceLength;
			} else {
				lengthMold = lengthMold + pieceList.get(0).length();
				result = result + pieceList.get(0);
			}
			pieceList.remove(0);

			if (!colorOrAction.isEmpty()) {
				if (lengthMold + colorOrAction.get(0).length() > pieceLength) {
					result = result + "\n" + colorOrAction.get(0);
					lengthMold = lengthMold + 1 + colorOrAction.get(0).length();
					lengthMold = lengthMold % pieceLength;
				} else {
					result = result + colorOrAction.get(0);
					lengthMold = lengthMold + 1 + colorOrAction.get(0).length();
				}
				colorOrAction.remove(0);
			}
		}
		return result;
	}

}

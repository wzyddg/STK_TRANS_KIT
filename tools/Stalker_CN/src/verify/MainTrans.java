package verify;

import java.awt.Robot;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.lsj.trans.*;

import util.BingWebTranslator;
import util.FlushRobot;
import util.GoogleWebTranslator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MainTrans {
	static String localDirSeparater = File.separator;
	static long currentTimeMillis = System.currentTimeMillis();
	static String transAPI = "bingn";
	static String oriLang = "ru";
	static String targetLang = "zh";
	static int sleepMilliSecond = 40;
	static ArrayList<String> keyList = new ArrayList<String>();
	static public String yandexKey = "";
	static int errorSleepMilliSecond = 2000;
	static boolean verbose = false;
	static HashMap<String, String> existingSentence = new HashMap<>();
	static MainTrans singelInstance = null;

	public static void main(String[] args) throws Exception {
		Class.forName("com.lsj.trans.BaiduDispatch");
		Class.forName("com.lsj.trans.GoogleDispatch");
		Class.forName("com.lsj.trans.YandexDispatch");
		Class.forName("com.lsj.trans.BingNeuralDispatch");
		singelInstance = new MainTrans();
		FlushRobot robot = new FlushRobot();
		Thread keepAlive = null;
		String oriAddress = "";
		File oriDir = null;

		if (args.length != 0 && args[0].startsWith("-trans")) {
			sleepMilliSecond = 1000;
		}

		if (args.length == 0) {
			printHelp();
		} else if (args[0].equals("-transT")) {
			// -transT api orilang targlang oridir [exdir [sleep [verb]]]
			if (args.length < 5) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			transAPI = args[1].toLowerCase();
			oriLang = args[2];
			targetLang = args[3];
			oriAddress = args[4];

			if (args.length > 6) {
				int sleep = 0;
				try {
					sleep = Integer.parseInt(args[6]);
				} catch (Exception e) {
					sleep = 100;
				}
				sleepMilliSecond = sleep;
			}

			if (args.length > 7) {
				verbose = true;
				keepAlive = new Thread(robot);
				keepAlive.start();
			}

			if (args.length > 5) {
				String[] exists = args[5].split(Pattern.quote("|"));
				for (int i = 0; i < exists.length; i++) {
					System.err.println("start" + exists[i]);
					mapExistingFile(exists[i]);
				}
			}

			ArrayList<String> finishedFiles = new ArrayList<String>();
			oriDir = new File(oriAddress);
			File transedDir = new File(oriDir.getPath() + localDirSeparater + "translated_" + transAPI);
			if (transedDir.exists()) {
				File[] transed = transedDir.listFiles();
				for (int i = 0; i < transed.length; i++) {
					finishedFiles.add(transed[i].getName());
				}
			}
			File[] oriXMLs = oriDir.listFiles();
			for (int i = 0; i < oriXMLs.length; i++) {
				if (oriXMLs[i].isFile() && !finishedFiles.contains(oriXMLs[i].getName())) {
					translateTextFile(oriXMLs[i]);
				}
			}
			System.out.println("all done! the translated files are in " + oriAddress + localDirSeparater + "translated_"
					+ transAPI);
		} else if (args[0].equals("-transG")) {
			// -transG api orilang targlang oridir [exdir [sleep [verb]]]
			if (args.length < 5) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			transAPI = args[1].toLowerCase();
			oriLang = args[2];
			targetLang = args[3];
			oriAddress = args[4];

			if (args.length > 6) {
				int sleep = 0;
				try {
					sleep = Integer.parseInt(args[6]);
				} catch (Exception e) {
					sleep = 100;
				}
				sleepMilliSecond = sleep;
			}

			if (args.length > 7) {
				verbose = true;
				keepAlive = new Thread(robot);
				keepAlive.start();
			}

			if (args.length > 5) {
				String[] exists = args[5].split(Pattern.quote("|"));
				for (int i = 0; i < exists.length; i++) {
					mapExistingFile(exists[i]);
				}
			}

			ArrayList<String> finishedFiles = new ArrayList<String>();
			oriDir = new File(oriAddress);
			File transedDir = new File(oriDir.getPath() + localDirSeparater + "translated_" + transAPI);
			if (transedDir.exists()) {
				File[] transed = transedDir.listFiles();
				for (int i = 0; i < transed.length; i++) {
					finishedFiles.add(transed[i].getName());
				}
			}
			File[] oriXMLs = oriDir.listFiles();
			for (int i = 0; i < oriXMLs.length; i++) {
				if (oriXMLs[i].isFile() && !finishedFiles.contains(oriXMLs[i].getName())) {
					translateGamePlayFile(oriXMLs[i]);
				}
			}
			System.out.println("all done! the translated files are in " + oriAddress + localDirSeparater + "translated_"
					+ transAPI);
		} else if (args[0].equals("-transS")) {
			// -transS api orilang targlang oridir [exdir [sleep [verb]]]
			if (args.length < 5) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			transAPI = args[1].toLowerCase();
			oriLang = args[2];
			targetLang = args[3];
			oriAddress = args[4];

			if (args.length > 6) {
				int sleep = 0;
				try {
					sleep = Integer.parseInt(args[6]);
				} catch (Exception e) {
					sleep = 100;
				}
				sleepMilliSecond = sleep;
			}

			if (args.length > 7) {
				verbose = true;
				keepAlive = new Thread(robot);
				keepAlive.start();
			}

			if (args.length > 5) {
				String[] exists = args[5].split(Pattern.quote("|"));
				for (int i = 0; i < exists.length; i++) {
					mapExistingFile(exists[i]);
				}
			}

			ArrayList<String> finishedFiles = new ArrayList<String>();
			oriDir = new File(oriAddress);
			File transedDir = new File(oriDir.getPath() + localDirSeparater + "translated_" + transAPI);
			if (transedDir.exists()) {
				File[] transed = transedDir.listFiles();
				for (int i = 0; i < transed.length; i++) {
					finishedFiles.add(transed[i].getName());
				}
			}
			File[] oriXMLs = oriDir.listFiles();
			for (int i = 0; i < oriXMLs.length; i++) {
				if (oriXMLs[i].isFile() && !finishedFiles.contains(oriXMLs[i].getName())) {
					translateScriptFileNew(oriXMLs[i]);
				}
			}
			System.out.println("all done! the translated files are in " + oriAddress + localDirSeparater + "translated_"
					+ transAPI);
		} else if (args[0].equals("-transL")) {
			// -transL api orilang targlang oridir [exdir [sleep [verb]]]
			if (args.length < 5) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			transAPI = args[1].toLowerCase();
			oriLang = args[2];
			targetLang = args[3];
			oriAddress = args[4];

			if (args.length > 6) {
				int sleep = 0;
				try {
					sleep = Integer.parseInt(args[6]);
				} catch (Exception e) {
					sleep = 100;
				}
				sleepMilliSecond = sleep;
			}

			if (args.length > 7) {
				verbose = true;
				keepAlive = new Thread(robot);
				keepAlive.start();
			}

			if (args.length > 5) {
				String[] exists = args[5].split(Pattern.quote("|"));
				for (int i = 0; i < exists.length; i++) {
					mapExistingFile(exists[i]);
				}
			}

			ArrayList<String> finishedFiles = new ArrayList<String>();
			oriDir = new File(oriAddress);
			File transedDir = new File(oriDir.getPath() + localDirSeparater + "translated_" + transAPI);
			if (transedDir.exists()) {
				File[] transed = transedDir.listFiles();
				for (int i = 0; i < transed.length; i++) {
					finishedFiles.add(transed[i].getName());
				}
			}
			File[] oriXMLs = oriDir.listFiles();
			for (int i = 0; i < oriXMLs.length; i++) {
				if (oriXMLs[i].isFile() && !finishedFiles.contains(oriXMLs[i].getName())) {
					translateLTXFileNew(oriXMLs[i]);
				}
			}
			System.out.println("all done! the translated files are in " + oriAddress + localDirSeparater + "translated_"
					+ transAPI);
		} else if (args[0].equals("-lack")) {
			// -lack oridir exdir [verb]
			if (args.length < 3) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			oriAddress = args[1];
			String[] exists = args[2].split(Pattern.quote("|"));
			for (int i = 0; i < exists.length; i++) {
				mapExistingFile(exists[i]);
			}
			if (args.length > 3) {
				verbose = true;
				keepAlive = new Thread(robot);
				keepAlive.start();
			}
			generateLackSentenceFile(oriAddress);
			System.out
					.println("all done! the generated file is " + oriAddress + localDirSeparater + "lackSentences.txt");
		} else if (args[0].equals("-diff")) {
			// -diff oridir curdir [verb]
			if (args.length < 3) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			oriAddress = args[1];
			String[] secondEdition = args[2].split(Pattern.quote("|"));
			for (int i = 0; i < secondEdition.length; i++) {
				mapExistingFile(secondEdition[i], "windows-1251");
			}
			if (args.length > 3) {
				verbose = true;
				keepAlive = new Thread(robot);
				keepAlive.start();
			}
			generateDifferentSentenceFile(oriAddress);
			System.out.println(
					"all done! thegenerated file is " + oriAddress + localDirSeparater + "differentSentences.txt");
		} else if (args[0].equals("-list")) {
			// -list filedir
			if (args.length < 2) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			String filesString = "";
			File[] oriXMLs = new File(args[1]).listFiles();
			for (int i = 0; i < oriXMLs.length; i++) {
				if (oriXMLs[i].isFile() && oriXMLs[i].getName().toLowerCase().endsWith(".xml")) {
					filesString = filesString + oriXMLs[i].getName().substring(0, oriXMLs[i].getName().length() - 4);
					if (i < oriXMLs.length - 1) {
						filesString = filesString + ", ";
					}
				}
			}
			System.out.println(filesString);
		} else if (args[0].equals("-toUtf8")) {
			// -list filedir
			if (args.length < 2) {
				System.err.println("too few parameters. use -h to see help.");
				return;
			}
			allToUTF8(args[1]);
		} else {
			printHelp();
		}
		robot.stop();
	}

	public static void printHelp() {
		System.out.println("\tSTALKER Automatic Machine-Translation System");
		System.out.println("\t\t\tby wzyddg");
		System.out.println();
		System.out.println("this software can do 5 things for the localization of the GSC STALKER series:");
		System.out.println("1.translate the text .xml files in gamedata\\config(s)\\text\\languageName.");
		System.out.println(
				"2.translate the gameplay .xml files in gamedata\\config(s)\\gameplay in case there are some sentences which aren't in gamedata\\config(s)\\text\\languageName.");
		System.out.println(
				"3.translate the script xml files in gamedata\\scripts, especially for pda news texts which aren't in gamedata\\config(s)\\text\\languageName.");
		System.out.println(
				"4.translate the ltx xml files in gamedata\\scripts\\config\\misc or something els, especially for quest items which aren't in gamedata\\config(s)\\text\\languageName.");
		System.out.println(
				"5.generate a file containing those sentences you haven't translated from the original files yet.");
		System.out.println(
				"6.generate a file containing same key with different contents between different versions of same MOD(sgm2.2 -> sgm2.2 lost soul or NLC7 -> NLC7.5).");
		System.out.println(
				"7.show a formatted string containing the names of .xml files in a folder so that you can paste the string to the localization.ltx (for the mods for 'Shadow of Chernobyl').");
		System.out.println("8.convert all files in a given folder from windows-1251 to utf-8.");
		System.out.println("9.call for help.");
		System.out.println();
		System.out.println("here are the instructions for the functions");
		System.out.println();
		System.out.println("1.-transT api orilang targlang oridir [exdir [sleep [verb]]]");
		System.out.println("2.-transG api orilang targlang oridir [exdir [sleep [verb]]]");
		System.out.println("3.-transS api orilang targlang oridir [exdir [sleep [verb]]]");
		System.out.println("4.-transL api orilang targlang oridir [exdir [sleep [verb]]]");
		System.out.println("5.-lack oridir exdir [verb]");
		System.out.println("6.-diff oridir newdir [verb]");
		System.out.println("7.-list filedir");
		System.out.println("8.-toUtf8 filedir");
		System.out.println("9.-h");
		System.out.println();
		System.out.println("Details for the parameter:");
		System.out.println("\t*parameters in [] is optional.");
		System.out.println("\t*the first parameter of the instruction is the name of function, just type as it is.");
		System.out.println("\t*api:which machine-translation engine you want to use, now we have:");
		System.out.println("\t\tbaidu\tas Baidu translation.");
		System.out.println("\t\tgoogle\tas Google translation.");
		System.out.println("\t\tyandex\tas Yandex translation.");
		System.out.println("\t\tbingn\tas Microsoft Neural translation.");
		System.out.println("\t*oriLang and targLang:original text language and target text language, now we have:");
		System.out.println("\t\ten as English.");
		System.out.println("\t\tru as Russian.");
		System.out.println("\t\tzh as Chinese Simplified.(for targLang only)");
		System.out.println("\t\tfr as French.(for targLang only)");
		System.out.println("\t*filedir:address of the folder containing the .xml files you want to get list of.");
		System.out.println("\t*oridir:address of the folder containing the original .xml text files.");
		System.out.println("\t*exdir:address of the folders containing the translated .xml text files.");
		System.out.println("\t\tusing this parameter can save you from redoing the works you've done before.");
		System.out.println("\t\tthis parameter supports multiple folder address, seperated by the single character |.");
		System.out.println(
				"\t\tcontents from latter folder will overwrite contents with same id before it, so you may wanna sort them from low priority to high priority.");
		System.out.println(
				"\t*newdir:address of the folders containing the .xml text files from new version of the mod.");
		System.out.println("\t\tthe generated file will contain sentences from newdir, not oridir.");
		System.out.println("\t\tswitch the position of oridir and newdir, the result will change.");
		System.out.println("\t\tthis parameter supports multiple folder address, seperated by the single character |.");
		System.out.println(
				"\t\tcontents from latter folder will overwrite contents with same id before it, so you may wanna sort them from low priority to high priority.");
		System.out.println(
				"\t*sleep:the time (in millisecond) you want to pause between the translation of two sentences.(around 100 is recommended.)");
		System.out.println("\t\tlonger pause time is more likely to keep you from triggering machine-human detection.");
		System.out.println(
				"\t*verb:any word at this place will enable verbose mode, you can see more details of processing now.");
		System.out.println();
		System.out.println("NOTE: ");
		System.out.println(
				"\tyou'll need to quote all the spaces in every folder address by yourself, I cannot do that for you.");
		System.out.println("\tevery original file needs to be saved in encoding windows-1251.");
		System.out.println("\talways use exdir if you have, it improves both efficiency and accuracy.");
		System.out.println(
				"\tI worked a lot on robust, but I won't guarantee a instant usable collection of translated .xml file, you may need to make a little adjustment yourself.");
	}

	// default UTF-8
	public static void mapExistingFile(String existingFolderAddress)
			throws ClassNotFoundException, IOException, InterruptedException {
		mapExistingFile(existingFolderAddress, "UTF-8");
	}

	public static void mapExistingFile(String existingFolderAddress, String encodingName)
			throws ClassNotFoundException, IOException, InterruptedException {
		if (!existingFolderAddress.equals("")) {
			File existingDir = new File(existingFolderAddress);
			File[] eFiles = existingDir.listFiles();
			for (int i = 0; eFiles != null && i < eFiles.length; i++) {
				verbose("started existing file:" + eFiles[i]);
				if (eFiles[i].isFile()) {
					existingSentence.putAll(getTextFileMap(
							existingFolderAddress + localDirSeparater + eFiles[i].getName(), encodingName));
					verbose("existing file " + eFiles[i].getName() + " done!");
				}
			}
		}
	}

	public static void generateDifferentSentenceFile(String oriAddr)
			throws ClassNotFoundException, IOException, InterruptedException {
		HashMap<String, String> oriSentence = new HashMap<>();

		if (!oriAddr.equals("")) {
			File oriDir = new File(oriAddr);
			File[] Files = oriDir.listFiles();
			for (int i = 0; Files != null && i < Files.length; i++) {
				if (Files[i].isFile()) {
					oriSentence
							.putAll(getTextFileMap(oriAddr + localDirSeparater + Files[i].getName(), "windows-1251"));
					verbose("ori file " + Files[i].getName() + " done!");
				}
			}
		}
		verbose("origin:" + oriSentence.size());
		verbose("current:" + existingSentence.size());

		LinkedList<String> diffKeys = new LinkedList<>();

		for (String key : oriSentence.keySet()) {
			if (existingSentence.containsKey(key)) {
				String oldSent = oriSentence.get(key);
				String newSent = existingSentence.get(key);
				if (newSent != null && !oldSent.equals(newSent)) {
					diffKeys.add(key);
				}
			}
		}
		verbose("different:" + diffKeys.size());
		String resString = "";
		Collections.sort(diffKeys);

		for (int i = 0; i < diffKeys.size(); i++) {
			String key = diffKeys.get(i);
			resString = resString + "<string id=\"" + key + "\">" + "\r\n" + "\t<text>" + existingSentence.get(key)
					+ "</text>" + "\r\n</string>\r\n";
		}
		writeToFile(resString, oriAddr + localDirSeparater + "differentSentences_new.txt", "utf-8");

		resString = "";
		for (int i = 0; i < diffKeys.size(); i++) {
			String key = diffKeys.get(i);
			resString = resString + "<string id=\"" + key + "\">" + "\r\n" + "\t<text>" + oriSentence.get(key)
					+ "</text>" + "\r\n</string>\r\n";
		}
		writeToFile(resString, oriAddr + localDirSeparater + "differentSentences_old.txt", "utf-8");
	}

	public static void generateLackSentenceFile(String oriAddr)
			throws ClassNotFoundException, IOException, InterruptedException {
		HashMap<String, String> oriSentence = new HashMap<>();

		if (!oriAddr.equals("")) {
			File oriDir = new File(oriAddr);
			File[] Files = oriDir.listFiles();
			for (int i = 0; Files != null && i < Files.length; i++) {
				if (Files[i].isFile()) {
					oriSentence
							.putAll(getTextFileMap(oriAddr + localDirSeparater + Files[i].getName(), "windows-1251"));
					verbose("ori file " + Files[i].getName() + " done!");
				}
			}
		}
		verbose("origin:" + oriSentence.size());
		verbose("exists:" + existingSentence.size());
		for (String string : existingSentence.keySet()) {
			oriSentence.remove(string);
		}
		verbose("lack:" + oriSentence.size());
		String resString = "";
		LinkedList<String> keys = new LinkedList<>();
		for (String key : oriSentence.keySet()) {
			keys.add(key);
		}
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			resString = resString + "<string id=\"" + key + "\">" + "\r\n" + "\t<text>" + oriSentence.get(key)
					+ "</text>" + "\r\n</string>\r\n";
		}
		writeToFile(resString, oriAddr + localDirSeparater + "lackSentences.txt", "utf-8");
	}

	public static boolean isSentence(String str) {
		if (str.matches("[\\s\\S]*[АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщъЫыьЭэЮюЯяЬ][\\s\\S]*")) {
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

	public class ShorterLatterComparator implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg1.toString().length() - arg0.toString().length();
		}

	}

	public static void translateGamePlayFile(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;

		String rusString = getFileContentString(rus.getParent() + localDirSeparater + rus.getName());

		String chsString = rusString;

		HashSet<String> sentences = new HashSet<>();

		Pattern p = Pattern
				.compile("<(?:text|bio|title|name)(?:| [ \\S]*?[^/]) *?>([\\s\\S]*?)</(?:text|bio|title|name)>");
		Matcher m = p.matcher(chsString);
		int i = 0;
		while (m.find()) {
			String thisSentence = m.group(1);
			if (!existingSentence.containsKey(thisSentence)) {
				if (isSentence(thisSentence)) {
					sentences.add(thisSentence);
				}
			}
			i++;
		}

		System.out.println("find " + i + " sentences,set get " + sentences.size());

		ArrayList<String> sortedList = new ArrayList<>();
		sortedList.addAll(sentences);
		Collections.sort(sortedList, singelInstance.new ShorterLatterComparator());

		// for CNPack generating
		String allGeneratedLines = "<string id=\"takemeplease\"> <text>";
		// for CNPack generating

		String string = "";
		boolean failFlag = false;
		for (Iterator<String> iterator = sortedList.iterator(); iterator.hasNext() || !"".equals(string);) {
			if (!failFlag) {
				string = iterator.next();
			}
			failFlag = false;
			try {
				String oriLine = string;
				String transtedLine = transToTarget(oriLine, targetLang);
				transtedLine = clearString(transtedLine);
				chsString = chsString.replaceAll(Pattern.quote(string), Matcher.quoteReplacement(transtedLine));

				// for CNPack generating
				allGeneratedLines = allGeneratedLines + transtedLine;
				// for CNPack generating

			} catch (Exception e) {
				failFlag = true;
				System.out.println("");
				e.printStackTrace();
				System.out.println(string);
				Thread.sleep(errorSleepMilliSecond);
				continue;
			}
			transNum++;
			string = "";
			System.out.print("" + transNum + ",");
		}
		System.out.println("");

		chsString = chsString.replaceAll("？", "?");

		// System.out.println(chsString);
		if (sentences.size() != 0) {
			writeToFile(chsString,
					rus.getParent() + localDirSeparater + "translated_" + transAPI + localDirSeparater + rus.getName(),
					"utf-8");
			// for CNPack generating
			allGeneratedLines = allGeneratedLines + "</text> </string>";
			writeToFile(allGeneratedLines, rus.getParent() + localDirSeparater + "GeneratedFileForCNPackMaking"
					+ localDirSeparater + "temp_" +rus.getName(), "utf-8");
			// for CNPack generating
		}

		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public static void translateScriptFileNew(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;

		String rusString = readStringFromFile(rus.getParent() + localDirSeparater + rus.getName(), "windows-1251");

		// minus
		String chsString = rusString.replaceAll("\\\\\\\\n", "\\\\n").replaceAll("(?<!\\\\)\\\\\"", "'");

		HashSet<String> sentences = new HashSet<>();

		Pattern p = Pattern.compile(
				"\"([АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщъЫыьЭэЮюЯяЬ @№«»,.?!-_QWERTYUIOPLKJHGFDSAZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789\\\\]*?)(?=\")");
		Matcher m = p.matcher(chsString);
		int i = 0;
		while (m.find()) {
			String thisSentence = m.group(1);
			if (!existingSentence.containsKey(thisSentence)) {
				if (isSentence(thisSentence) && thisSentence.matches(
						"[\\s\\S]*?[АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщъЫыьЭэЮюЯяЬ]+[\\s\\S]*?")) {
					sentences.add(thisSentence);
				}
			}
			i++;
		}

		System.out.println("find " + i + " sentences,set get " + sentences.size());

		ArrayList<String> sortedList = new ArrayList<>();
		sortedList.addAll(sentences);
		Collections.sort(sortedList, singelInstance.new ShorterLatterComparator());

		String string = "";
		boolean failFlag = false;
		f1: for (Iterator<String> iterator = sortedList.iterator(); iterator.hasNext() || !"".equals(string);) {
			if (!failFlag) {
				string = iterator.next();
			}
			failFlag = false;
			try {
				String oriLine = string;
				String transtedLine = "";

				Pattern p1 = Pattern.compile(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");
				Matcher m1 = p1.matcher(oriLine);
				LinkedList<String> colorOrAction = new LinkedList<>();
				while (m1.find()) {
					colorOrAction.add(m1.group(0));
				}
				String[] pieces = string.split(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");

				verbose(string);
				verbose(" get " + pieces.length + " pieces.");
				try {
					for (int j = 0; j < pieces.length; j++) {
						transtedLine = transtedLine + transToTarget(pieces[j], targetLang);
						if (!colorOrAction.isEmpty()) {
							transtedLine = transtedLine + colorOrAction.get(0);
							colorOrAction.remove(0);
						}
						System.err.print("(." + (j + 1) + ")");
					}
					transtedLine = clearString(transtedLine).replaceAll("\\\\n", "\\\\\\\\n");
					if (pieces.length == 0) {
						transtedLine = string;
						System.err.print("(.1)");
					}
				} catch (Exception e) {
					failFlag = true;
					System.out.println("");
					System.err.println(e);
					Thread.sleep(errorSleepMilliSecond);
					continue f1;
				}

				verbose(transtedLine);
				chsString = chsString.replaceAll(Pattern.quote("\"" + string + "\""),
						Matcher.quoteReplacement("\"" + transtedLine + "\""));
			} catch (Exception e) {
				failFlag = true;
				System.out.println("");
				e.printStackTrace();
				System.out.println(string);
				Thread.sleep(errorSleepMilliSecond);
				continue;
			}
			transNum++;
			string = "";
			System.out.print("" + transNum + ",");
		}
		System.out.println("");

		chsString = chsString.replaceAll("？", "?");

		// System.out.println(chsString);
		if (sentences.size() != 0) {
			writeToFile(chsString,
					rus.getParent() + localDirSeparater + "translated_" + transAPI + localDirSeparater + rus.getName(),
					"utf-8");
		}

		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public static void translateLTXFileNew(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;

		String rusString = readStringFromFile(rus.getParent() + localDirSeparater + rus.getName(), "windows-1251");

		// minus
		String chsString = rusString.replaceAll("[\\s]*?(?=\\n)", "").replaceAll(";[^\\n]*?(?=\\n)", "");
		// .replaceAll("(?<!\\\\)\\\\\"", "'");

		HashSet<String> sentences = new HashSet<>();

		Pattern p = Pattern.compile(
				"(?<== )([АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщъЫыьЭэЮюЯяЬ @№«»,.?!-_QWERTYUIOPLKJHGFDSAZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789\\\\]*?)(?=\\n)");
		Matcher m = p.matcher(chsString);
		int i = 0;
		while (m.find()) {
			String thisSentence = m.group(1);
			if (!existingSentence.containsKey(thisSentence)) {
				if (isSentence(thisSentence) && thisSentence.matches(
						"[\\s\\S]*?[АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщъЫыьЭэЮюЯяЬ]+[\\s\\S]*?")) {
					sentences.add(thisSentence);
				}
			}
			i++;
		}

		System.out.println("find " + i + " sentences,set get " + sentences.size());

		ArrayList<String> sortedList = new ArrayList<>();
		sortedList.addAll(sentences);
		Collections.sort(sortedList, singelInstance.new ShorterLatterComparator());

		String string = "";
		boolean failFlag = false;
		f1: for (Iterator<String> iterator = sortedList.iterator(); iterator.hasNext() || !"".equals(string);) {
			if (!failFlag) {
				string = iterator.next();
			}
			failFlag = false;
			try {
				String oriLine = string;
				String transtedLine = "";

				Pattern p1 = Pattern.compile(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");
				Matcher m1 = p1.matcher(oriLine);
				LinkedList<String> colorOrAction = new LinkedList<>();
				while (m1.find()) {
					colorOrAction.add(m1.group(0));
				}
				String[] pieces = string.split(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");

				verbose(string);
				verbose(" get " + pieces.length + " pieces.");
				try {
					for (int j = 0; j < pieces.length; j++) {
						transtedLine = transtedLine + transToTarget(pieces[j], targetLang);
						if (!colorOrAction.isEmpty()) {
							transtedLine = transtedLine + colorOrAction.get(0);
							colorOrAction.remove(0);
						}
						System.err.print("(." + (j + 1) + ")");
					}
					// transtedLine = clearString(transtedLine).replaceAll("\\\\n","\\\\\\\\n");
					if (pieces.length == 0) {
						transtedLine = string;
						System.err.print("(.1)");
					}
				} catch (Exception e) {
					failFlag = true;
					System.out.println("");
					System.err.println(e);
					Thread.sleep(errorSleepMilliSecond);
					continue f1;
				}

				verbose(transtedLine);
				chsString = chsString.replaceAll(Pattern.quote(string), Matcher.quoteReplacement(transtedLine));
			} catch (Exception e) {
				failFlag = true;
				System.out.println("");
				e.printStackTrace();
				System.out.println(string);
				Thread.sleep(errorSleepMilliSecond);
				continue;
			}
			transNum++;
			string = "";
			System.out.print("" + transNum + ",");
		}
		System.out.println("");

		chsString = chsString.replaceAll("？", "?");
		if (sentences.size() != 0) {
			writeToFile(chsString,
					rus.getParent() + localDirSeparater + "translated_" + transAPI + localDirSeparater + rus.getName(),
					"utf-8");
		}

		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public boolean firstisLonger(String first, String second) {
		return first.length() > second.length();
	}

	public static void translateTextFile(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;

		String rusString = getFileContentString(rus.getParent() + localDirSeparater + rus.getName());
		String chsString = rusString;

		HashMap<String, String> sentences = getTextFileMap(rus.getParent() + localDirSeparater + rus.getName(),
				"windows-1251");
		System.out.println("find " + sentences.size() + " sentences.");

		String string = "";
		String key = "";
		boolean failFlag = false;
		f1: for (Iterator<String> iterator = sentences.keySet().iterator(); iterator.hasNext() || !"".equals(string);) {
			if (!failFlag) {
				key = iterator.next();
				string = sentences.get(key);
			}
			failFlag = false;

			String oriLine = string;
			String transtedLine = existingSentence.get(key);

			if (transtedLine != null && !"".equals(transtedLine)) {
				transtedLine = clearString(transtedLine);
				verbose(key + " get quick.");
			} else {
				transtedLine = "";
				Pattern p1 = Pattern.compile(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");
				Matcher m1 = p1.matcher(oriLine);
				LinkedList<String> colorOrAction = new LinkedList<>();
				while (m1.find()) {
					colorOrAction.add(m1.group(0));
				}
				String[] pieces = string.split(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");

				verbose(key + " : " + string);
				verbose(" get " + pieces.length + " pieces.");
				try {
					for (int j = 0; j < pieces.length; j++) {
						transtedLine = transtedLine + transToTarget(pieces[j], targetLang);
						if (!colorOrAction.isEmpty()) {
							transtedLine = transtedLine + colorOrAction.get(0);
							colorOrAction.remove(0);
						}
						System.err.print("(." + (j + 1) + ")");
					}
					transtedLine = clearString(transtedLine);
					if (pieces.length == 0) {
						transtedLine = string;
						System.err.print("(.1)");
					}
				} catch (Exception e) {
					failFlag = true;
					System.out.println("");
					System.err.println(e);
					System.err.println(key);
					Thread.sleep(errorSleepMilliSecond);
					continue f1;
				}
			}

			chsString = chsString.replaceAll("<string id=\"" + key + "\">\\s*?<text>([\\s\\S]*?)</text>\\s*?</string>",
					"<string id=\"" + key + "\">\n\t\t<text>" + Matcher.quoteReplacement(transtedLine)
							+ "</text>\n\t</string>");
			transNum++;
			System.out.print("" + transNum + ",");
			verbose("translated to: " + transtedLine);
			string = "";
		}
		System.out.println("");

		chsString = chsString.replaceAll("encoding=\"(.*?)\"", "encoding=\"UTF-8\"").replaceAll("‘", "'")
				.replaceAll("’", "'").replaceAll("#include \"text\\\\[a-zA-Z]{3}", "#include \"text\\\\chs")
				.replaceAll("（", Matcher.quoteReplacement("(")).replaceAll("）", Matcher.quoteReplacement(")"))
				.replaceAll("“", "'").replaceAll("？", "?").replaceAll("”", "'");

		writeToFile(chsString,
				rus.getParent() + localDirSeparater + "translated_" + transAPI + localDirSeparater + rus.getName(),
				"utf-8");
		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public static void allToUTF8(String oriAddr) throws IOException {
		File dir = new File(oriAddr);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				String content = readStringFromFile(files[i].getPath(), "windows-1251");
				writeToFile(content,
						files[i].getParent() + localDirSeparater + "utf" + localDirSeparater + files[i].getName(),
						"utf-8");
				System.out.println(files[i].getName() + " done!");
			}
		}
	}

	public static String getFileContentString(String fileAddress) throws IOException, InterruptedException {
		return getFileContentString(fileAddress, "windows-1251");
	}

	public static String readStringFromFile(String fileAddress, String encodingName) throws IOException {
		File file = new File(fileAddress);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encodingName));
		String string = "";
		String tmp = "";
		for (; (tmp = reader.readLine()) != null;) {
			if (!tmp.matches("[\\s]*?")) {
				string = string + tmp + "\n";
			}
		}
		reader.close();
		return string;
	}

	public static String getFileContentString(String fileAddress, String encodingName)
			throws IOException, InterruptedException {
		if (sleepMilliSecond == 1000) { // 1000 is default
			Thread.sleep(sleepMilliSecond * 1200);
			// go ahead and wait for 20 minutes for each file, you lazy shit. :)
		} else {
			verbose("Thanks for reading the help document.");
		}

		File textFile = new File(fileAddress);
		File cachedFile = new File(textFile.getParent() + localDirSeparater + "string_cache" + localDirSeparater
				+ textFile.getName() + ".wzystr");
		cachedFile.getParentFile().mkdirs();
		String thisString = "";
		if (cachedFile.exists()) {
			verbose("string chache exist");
			try {
				thisString = readStringFromFile(cachedFile.getParent() + localDirSeparater + cachedFile.getName(),
						"utf-8");
			} catch (Exception e) {
				cachedFile.delete();
				return getFileContentString(fileAddress, encodingName);
			}
			System.out.println(fileAddress + " got from cache.");
		} else {
			verbose("string chache not exist");
			String string = readStringFromFile(fileAddress, encodingName);
			string = clearXMLString(string);
			thisString = string;
			writeToFile(thisString, cachedFile.getParent() + localDirSeparater + cachedFile.getName(), "utf-8");
		}
		return thisString;
	}

	public static String clearXMLString(String str) {
		return str.replaceAll("[\\s\\S]*?<?xml\\s", "<?xml ").replaceAll("<!--[\\s\\S]*?-->", "")
				// .replaceAll("encoding=\"(.*?)\"", "encoding=\"UTF-8\"")
				.replaceAll("？", "?").replaceAll(Pattern.quote(">>"), Matcher.quoteReplacement(">"))
				.replaceAll(Pattern.quote("<<"), Matcher.quoteReplacement("<"));
	}

	public static String clearString(String str) {
		str = str.replaceAll("<!--[\\s\\S]*?-->", "").replaceAll("(?:&apos;|&quot;)", Matcher.quoteReplacement("'"))
				.replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("'")).replaceAll("，", ",")
				.replaceAll("：", ":").replaceAll("。", Matcher.quoteReplacement(".")).replaceAll("(?:&lt;|&gt;)", "<")
				.replaceAll("(?:</|/>)", "").replaceAll("(?:<|>)", "")
				.replaceAll("\\\\[\\s]+?n", Matcher.quoteReplacement("\\n"))
				.replaceAll("\\\\n(?:\\\\n|\\s)*\\\\n", Matcher.quoteReplacement("\\n"))
				.replaceAll(Pattern.quote(".."), Matcher.quoteReplacement("."))
				.replaceAll("[.]{2,}", Matcher.quoteReplacement("..."));

		return str;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getTextFileMap(String fileAddress, String encodingName)
			throws IOException, ClassNotFoundException, InterruptedException {
		HashMap<String, String> map = null;
		File textFile = new File(fileAddress);
		File serializedMap = new File(textFile.getParent() + localDirSeparater + "map_cache" + localDirSeparater
				+ textFile.getName() + ".wzymap");
		serializedMap.getParentFile().mkdirs();
		if (serializedMap.exists()) {
			verbose("map chache exist");
			ObjectInputStream objectInputStream = null;
			try {
				objectInputStream = new ObjectInputStream(new FileInputStream(serializedMap));
				Object o = objectInputStream.readObject();
				map = (HashMap<String, String>) o;
			} catch (Exception e) {
				e.printStackTrace();
				objectInputStream.close();
				serializedMap.delete();
				return getTextFileMap(fileAddress, encodingName);
			}
			objectInputStream.close();
			System.out.println(fileAddress + " mapped from cache.");
		} else {
			verbose("map chache not exist");
			map = new HashMap<>();
			String string = getFileContentString(fileAddress, encodingName);
			Pattern p = Pattern.compile(
					"<string[\\s]+?id[ ]?=[ ]?\"([a-zA-Z0-9_.'/, -]*?)\"[ ]?>\\s*?<text>([\\s\\S]*?)</text>\\s*?</string>");
			Matcher m = p.matcher(string);
			while (m.find()) {
				map.put(m.group(1), m.group(2));
			}
			ObjectOutputStream objectOutputStream = null;
			try {
				objectOutputStream = new ObjectOutputStream(new FileOutputStream(serializedMap));
				objectOutputStream.writeObject(map);
			} catch (Exception e) {
				e.printStackTrace();
			}
			objectOutputStream.flush();
			objectOutputStream.close();
		}
		return map;
	}

	public static void writeToFile(String content, String fileAddress, String encodeingName) throws IOException {
		File resultFile = new File(fileAddress);
		resultFile.getParentFile().mkdirs();

		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(resultFile), encodeingName));
		writer.write(content);
		writer.flush();
		writer.close();
	}

	public static HashMap<String, String> getTextFileMap(String fileAddress)
			throws IOException, ClassNotFoundException, InterruptedException {
		return getTextFileMap(fileAddress, "UTF-8");
	}

	public static String transToTarget(String sentence, String targ) throws Exception {
		String translated = "";

		// when return the origin string
		if (sentence.matches("[\\s0-9,.!?]*?")) { // only numbers and punc
			return sentence;
		}
		if ("googleweb".equals(transAPI)) {
			translated = GoogleWebTranslator.translate(sentence, targ);
		} else if ("bingweb".equals(transAPI)) {
			translated = BingWebTranslator.translate(sentence, targ);
		} else {
			try {
				translated = Dispatch.Instance(transAPI).Trans(oriLang, targ, sentence);
			} catch (Exception e) {
				System.err.println(e);
				if ("yandex".equals(transAPI) && e.getMessage().toLowerCase().contains("[\"text\"]")) {
					updateYandexKey();
					System.out.println("switched to another key.");
					throw e;
				}
				if ("bingn".equals(transAPI) && e.getMessage().toLowerCase().contains("[\"resultNMT\"]")) {
					System.err.println("bing nmt failed");
					throw e;
				}
			}
			Thread.sleep(sleepMilliSecond);
		}
		if (translated.matches("\\s*?") && !sentence.matches("\\s*?")) {
			throw new Exception("return a empty string from: " + sentence);
		}
		return translated;
	}

	public static void updateYandexKey() throws IOException {
		int currentIndex = keyList.indexOf(yandexKey);
		if (currentIndex >= 0 && currentIndex + 1 < keyList.size()) {
			yandexKey = keyList.get(currentIndex + 1);
			return;
		}
		String keysString = clearXMLString(readStringFromFile("yandexKeys.xml", "utf-8"));
		Pattern p = Pattern.compile("<key>(.*?)</key>");
		Matcher m = p.matcher(keysString);
		while (m.find()) {
			keyList.add(m.group(1));
		}
		yandexKey = keyList.get(0);
	}

	public static boolean goThroughIDs(File chs) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = builderFactory.newDocumentBuilder();
		Document chsDoc = builder.parse(chs);
		NodeList chsStrings = chsDoc.getElementsByTagName("string");

		// show what in rus not in chs
		for (int i = 0; i < chsStrings.getLength(); i++) {
			Element chsE = (Element) chsStrings.item(i);
			System.out.println(chsE.getAttribute("id"));
		}

		return true;
	}

	public static void findPattern(String fileAddress, String encodingName, String pattern, String unxpctdSuffix,
			String unxpctdKeyPart) throws ClassNotFoundException, IOException, InterruptedException {
		HashMap<String, String> aHashMap = getTextFileMap(fileAddress, encodingName);
		for (Iterator iterator = aHashMap.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			String value = aHashMap.get(key);
			Pattern nums = Pattern.compile(pattern);
			Matcher matcher = nums.matcher(value);
			f1: for (int i = 0; matcher.find(); i++) {
				if ((unxpctdSuffix.equals("") || !matcher.group(0).endsWith(unxpctdSuffix))
						&& (unxpctdKeyPart.equals("") || !key.contains(unxpctdKeyPart))) {
					// System.err.println(key);
					System.out.println(value);
					break f1;
				}
			}
		}
	}

	public static void verbose(String content) {
		if (verbose) {
			System.out.println(content);
		}
	}

	public static void translateStringArray(String[] strs) throws Exception {
		String string = "";
		boolean failFlag = false;
		f1: for (int i = -1; i < strs.length || !"".equals(string);) {
			if (!failFlag) {
				i++;
				if (i >= strs.length)
					break f1;
				string = strs[i];
			}
			failFlag = false;

			String transtedLine = null;

			if (transtedLine != null && !"".equals(transtedLine)) {
				transtedLine = clearString(transtedLine);
			} else {
				transtedLine = "";
				Pattern p1 = Pattern.compile(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");
				Matcher m1 = p1.matcher(string);
				LinkedList<String> colorOrAction = new LinkedList<>();
				while (m1.find()) {
					colorOrAction.add(m1.group(0));
				}
				String[] pieces = string.split(
						"(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?|\\[[a-zA-Z%]\\])");

				verbose(" get " + pieces.length + " pieces.");
				try {
					for (int j = 0; j < pieces.length; j++) {
						transtedLine = transtedLine + transToTarget(pieces[j], targetLang);
						if (!colorOrAction.isEmpty()) {
							transtedLine = transtedLine + colorOrAction.get(0);
							colorOrAction.remove(0);
						}
					}
					transtedLine = clearString(transtedLine);
					if (pieces.length == 0) {
						transtedLine = string;
					}
				} catch (Exception e) {
					e.printStackTrace();
					failFlag = true;
					Thread.sleep(errorSleepMilliSecond);
					continue f1;
				}
			}

			System.out.println("\"" + transtedLine.replaceAll("‘", "'").replaceAll("’", "'")
					.replaceAll("（", Matcher.quoteReplacement("(")).replaceAll("）", Matcher.quoteReplacement(")"))
					.replaceAll("“", "'").replaceAll("？", "?").replaceAll("”", "'").replaceAll("。", ".")
					.replaceAll("，", ",").replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("\\\""))
					.replaceAll(Pattern.quote("\\n"), Matcher.quoteReplacement("\\\\n")) + "\",");
			string = "";
		}
		System.out.println("");
		System.out.println("over!");
	}
}

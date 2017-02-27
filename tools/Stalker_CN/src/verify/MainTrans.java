package verify;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.lsj.trans.*;

import util.BingWebTranslator;
import util.GoogleWebTranslator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MainTrans {
	static int lackFileNum = 0;
	// static String localDirSeparater ="\\";
	static String localDirSeparater = "/";
	static long currentTimeMillis = System.currentTimeMillis();
	static String transAPI = "baidu";
	static String oriLang = "auto";
	static int sleepMilliSecond = 0;
	static ArrayList<String> keyList = new ArrayList<String>();
	static public String yandexKey = "";
	static int errorSleepMilliSecond = 2000;
	static boolean verbose = false; 
	static HashMap<String, String> existingSentence = new HashMap<>();

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			transAPI = args[0].substring(1).toLowerCase();
		}
		if (args.length > 1) {
			int sleep = 0;
			try {
				sleep = Integer.parseInt(args[1].substring(1));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				sleep = 300;
			}
			sleepMilliSecond = sleep;
		}
		Class.forName("com.lsj.trans.BaiduDispatch");
		Class.forName("com.lsj.trans.GoogleDispatch");
		Class.forName("com.lsj.trans.YandexDispatch");
		Class.forName("com.lsj.trans.BingNeuralDispatch");

		// System.out.println(Dispatch.Instance("Yandex").Trans("ru", "zh",
		// "Внатуре, спасибо тебе, братан. Короче, приходил сюда один такой
		// сталкер, ну... мы с ним ничего не делали, честно. Он спустился к нам
		// на базу к трубам вниз, так тут откуда-то кровосос взялся, да напал на
		// наших людей, уволок двоих туда в логово двоих, включая его. Братана
		// моего покоцало, я его сюда вытащил, кровь никак не остановить. Если
		// ищешь его, спускайся вниз, только будь осторожен, опасайся той
		// твари."));
		// TODO Auto-generated method stub
		// File rusDir = new File("D:\\AZMtext\\gameplay");
		String existingFolderAddress = "D:\\SGM2.2_LostSoul_CNPack_Complete\\chs\\not_used_ssssnow";
		String oriAddress = "D:\\SGM2.2_LostSoul_CNPack_Complete\\lack";
		String chsAddress = "D:\\SGM2.2_LostSoul_CNPack_Complete\\chs";
		File chsDir = new File(chsAddress);
		File oriDir = new File(oriAddress);
//		mapExistingFile(existingFolderAddress);
//		existingFolderAddress = "D:\\SGM2.2_LostSoul_CNPack_Complete\\chs\\inc";
//		mapExistingFile(existingFolderAddress);
//		existingFolderAddress = "D:\\SGM2.2_LostSoul_CNPack_Complete\\chs";
//		mapExistingFile(existingFolderAddress);
		// File chsDir = new
		// File("/Users/wzy/Desktop/SGM2.2_LostSoul_CNPack_Complete/chs");
		ArrayList<String> finishedFiles = new ArrayList<String>();
		File transedDir = new File(oriDir.getPath() + localDirSeparater + "translated_" + transAPI);
		if (transedDir.exists()) {
			File[] transed = transedDir.listFiles();
			for (int i = 0; i < transed.length; i++) {
				finishedFiles.add(transed[i].getName());
			}
		}

		String filesString = "";
		
		File[] oriXMLs = oriDir.listFiles();
		for (int i = 0; i < oriXMLs.length; i++) {
			if (oriXMLs[i].isFile() && !finishedFiles.contains(oriXMLs[i].getName())) {
				// File chs = new File(chsDir.getPath() + localDirSeparater +
				// rusXMLs[i].getName());
				// System.out.println(chs.getName()+" : ");
				// if (keyToKey(chs, rusXMLs[i])) {
				// return;
				// }
				// Thread.sleep(100);
				translateTextFile(oriXMLs[i]);
			}
			if (oriXMLs[i].isFile())
				filesString = filesString+oriXMLs[i].getName().split("[. ]")[0]+", ";
		}
//		System.out.println(filesString);
//		generateLackSentenceFile(oriAddress);
//		generateLackSentenceFile("D:\\HoMtext\\rus");
		System.out.println("lackFileNum:" + lackFileNum);
	}
	
	public static void mapExistingFile(String existingFolderAddress) throws ClassNotFoundException, IOException {
		if (!existingFolderAddress.equals("")) {
			File existingChsDir = new File(existingFolderAddress);
			File[] eFiles = existingChsDir.listFiles();
			for (int i = 0; eFiles!=null && i < eFiles.length; i++) {
				if (eFiles[i].isFile()) {
					existingSentence.putAll(getTextFileMap(existingFolderAddress + localDirSeparater + eFiles[i].getName()));
					verbose("existing file "+eFiles[i].getName()+" done!");
				}

			}
		}
	}

	public static boolean keyToKey(File chs, File rus) throws Exception {
		// String checkKeyword = "尼特罗";

		String chsString = getFileContentString(chs.getParent() + localDirSeparater + chs.getName(), "utf-8");
		String rusString = getFileContentString(chs.getParent() + localDirSeparater + rus.getName());

		// if (chsString.contains(checkKeyword)) {
		// throw new Exception(checkKeyword + " exists in " + chs.getName());
		// }
		ArrayList<String> rusIDs = new ArrayList<>();
		ArrayList<String> chsIDs = new ArrayList<>();

		boolean flag = false;
		Pattern p = Pattern.compile("id=\"(.*?)\"");
		Matcher m = p.matcher(rusString);
		while (m.find()) {
			rusIDs.add(m.group(1));
		}
		m = p.matcher(chsString);
		while (m.find()) {
			String found = m.group(1);
			if (chsIDs.contains(found)) {
				throw new Exception("chsID: " + found + " duplicated in " + chs.getName());
			}
			chsIDs.add(found);
			rusIDs.remove(m.group(1));
		}
		m = p.matcher(rusString);
		while (m.find()) {
			chsIDs.remove(m.group(1));
		}

		for (Iterator<String> iterator = rusIDs.iterator(); iterator.hasNext();) {
			if (!flag) {
				System.out.println(chs.getName() + ":");
				flag = true;
				lackFileNum++;
			}
			String string = (String) iterator.next();
			System.err.println(string);
		}
		for (Iterator<String> iterator = chsIDs.iterator(); iterator.hasNext();) {
			if (!flag) {
				System.out.println(rus.getName() + ":");
				flag = true;
			}
			String string = iterator.next();
			System.out.println(string);
		}

		return flag;
	}

	public static void generateLackSentenceFile(String oriAddr) throws ClassNotFoundException, IOException {
		HashMap<String, String> oriSentence = new HashMap<>();
		
		if (!oriAddr.equals("")) {
			File oriDir = new File(oriAddr);
			File[] Files = oriDir.listFiles();
			for (int i = 0; Files!=null && i < Files.length; i++) {
				if (Files[i].isFile()) {
					oriSentence.putAll(getTextFileMap(oriAddr + localDirSeparater + Files[i].getName(), "windows-1251"));
					verbose("ori file "+Files[i].getName()+" done!");
				}
			}
		}
		verbose("ori:"+oriSentence.size());
		verbose("exi:"+existingSentence.size());
		for (String string : existingSentence.keySet()) {
			oriSentence.remove(string);
		}
		verbose(""+oriSentence.size());
		String resString = "";
		LinkedList<String> keys = new LinkedList<>();
		for (String key : oriSentence.keySet()) {
			keys.add(key);
		}
		Collections.sort(keys);
		for (int i=0;i<keys.size();i++) {
			String key = keys.get(i);
			resString = resString + "<string id=\""+key+"\">" + "\r\n" +"\t<text>"+oriSentence.get(key)+"</text>"+ "\r\n</string>\r\n";
		}
		writeToFile(resString, oriAddr+localDirSeparater+"lackSentences.txt", "utf-8");
	}
	
	public static boolean isSentence(String str) {
		return (!str.matches("\\s*?")) && (!str.matches("[-.a-zA-Z0-9_,']*?"));
	}

	public static void translateGamePlayFile(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;

		String rusString = getFileContentString(rus.getParent() + localDirSeparater + rus.getName());

		String chsString = rusString;

		HashSet<String> sentences = new HashSet<>();
		Pattern p = Pattern.compile("<text(?:| [ \\S]*?[^/]) *?>([\\s\\S]*?)</text>");
		Matcher m = p.matcher(chsString);
		int i = 0;
		while (m.find()) {
			if (isSentence(m.group(1))) {
				sentences.add(m.group(1));
			}
			i++;
		}
		System.out.println("find " + i + " sentences,set get " + sentences.size());

		String string = "";
		boolean failFlag = false;
		for (Iterator<String> iterator = sentences.iterator(); iterator.hasNext() || !"".equals(string);) {
			if (!failFlag) {
				string = iterator.next();
			}
			failFlag = false;
			try {
				String oriLine = string;
				String transtedLine = transToCN(oriLine);
				transtedLine = clearString(transtedLine);
				chsString = chsString.replaceAll(Pattern.quote(string), transtedLine);
			} catch (Exception e) {
				failFlag = true;
				System.out.println("");
				System.err.println(e);
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
		if(sentences.size()==0)
			writeToFile("", rus.getParent() + localDirSeparater + "translated_" + transAPI + localDirSeparater + rus.getName(), "utf-8");
		else
			writeToFile(chsString, rus.getParent() + localDirSeparater + "translated_" + transAPI + localDirSeparater + rus.getName(), "utf-8");

		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public static void translateTextFile(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;
		
		String rusString = getFileContentString(rus.getParent() + localDirSeparater + rus.getName());
		String chsString = rusString;

		HashMap<String, String> sentences = getTextFileMap(rus.getParent() + localDirSeparater + rus.getName(), "windows-1251");
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

			if (transtedLine!=null&&!"".equals(transtedLine)) {
				transtedLine = clearString(transtedLine);
				verbose("this one get quick.");
			}else {
				transtedLine = "";
				Pattern p1 = Pattern
						.compile("(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?)");
				Matcher m1 = p1.matcher(oriLine);
				LinkedList<String> colorOrAction = new LinkedList<>();
				while (m1.find()) {
					colorOrAction.add(m1.group(0));
				}
				String[] pieces = string
						.split("(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?)");
				verbose("this sentence get "+pieces.length+" pieces.");
				try {
					for (int j = 0; j < pieces.length; j++) {
						transtedLine = transtedLine + transToCN(pieces[j]);
						if (!colorOrAction.isEmpty()) {
							transtedLine = transtedLine + colorOrAction.get(0);
							colorOrAction.remove(0);
						}
						System.err.print("(." + (j + 1) + ")");
					}
					transtedLine = clearString(transtedLine);
					if(pieces.length==0){
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

			chsString = chsString.replaceAll(
					"<string id=\"" + key + "\">\\s*?<text>([\\s\\S]*?)</text>\\s*?</string>",
					"<string id=\"" + key + "\">\n\t\t<text>" + Matcher.quoteReplacement(transtedLine)
							+ "</text>\n\t</string>");
			transNum++;
			System.out.print("" + transNum + ",");
			verbose(key+" : "+string+" → "+transtedLine);
			string = "";
		}
		System.out.println("");
		
		chsString = chsString
				.replaceAll("，", ",").replaceAll("：", ":").replaceAll("。", Matcher.quoteReplacement(".")).replaceAll("‘", "'").replaceAll("’", "'")
				.replaceAll("（", Matcher.quoteReplacement("(")).replaceAll("）", Matcher.quoteReplacement(")")).replaceAll("“", "'").replaceAll("”", "'");

		writeToFile(chsString, rus.getParent() + localDirSeparater + "translated_" + transAPI + localDirSeparater + rus.getName(), "utf-8");
		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public static String getFileContentString(String fileAddress) throws IOException {
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
	
	public static String getFileContentString(String fileAddress, String encodingName) throws IOException {
		HashMap<String, String> map = null;
		File textFile = new File(fileAddress);
		File cachedFile = new File(textFile.getParent()+localDirSeparater+"string_cache"+localDirSeparater+textFile.getName()+".wzystr");
		cachedFile.getParentFile().mkdirs();
		String thisString = "";
		if (cachedFile.exists()) {
			verbose("string chache exist");
			try {
				thisString = readStringFromFile(cachedFile.getParent()+localDirSeparater+cachedFile.getName(), "utf-8");
			} catch (Exception e) {
				cachedFile.delete();
				return getFileContentString(fileAddress, encodingName);
			}
			System.out.println(fileAddress+" got from cache.");
		}else {
			verbose("string chache not exist");
			String string = readStringFromFile(fileAddress, encodingName);
			string = clearXMLString(string);
			thisString = string;
			writeToFile(thisString, cachedFile.getParent()+localDirSeparater+cachedFile.getName(), "utf-8");
		}
		return thisString;
	}
	
	public static String clearXMLString(String str) {
		return str.replaceAll("[\\s\\S]*?<?xml\\s", "<?xml ").replaceAll("<!--[\\s\\S]*?-->", "")
				.replaceAll("encoding=\"(.*?)\"", "encoding=\"UTF-8\"").replaceAll("？", "?"); 
	}
	
	public static String clearString(String str) {
		str = str.replaceAll("<!--[\\s\\S]*?-->", "").replaceAll("(?:&apos;|&quot;)", Matcher.quoteReplacement("'"))
				.replaceAll(Pattern.quote("\""), Matcher.quoteReplacement("'"))
//				.replaceAll("，", ",").replaceAll("：", ":").replaceAll("。", Matcher.quoteReplacement("."))
				.replaceAll("(?:&lt;|&gt;)", "<").replaceAll("(?:</|/>)", "").replaceAll("(?:<|>)", "")
				.replaceAll("\\\\[\\s]+?n", Matcher.quoteReplacement("\\n")).replaceAll("\\\\n(?:\\\\n|\\s)*\\\\n", Matcher.quoteReplacement("\\n"));
		
		return str;
	}
	
	public static HashMap<String, String> getTextFileMap(String fileAddress, String encodingName) throws IOException, ClassNotFoundException {
		HashMap<String, String> map = null;
		File textFile = new File(fileAddress);
		File serializedMap = new File(textFile.getParent()+localDirSeparater+"map_cache"+localDirSeparater+textFile.getName()+".wzymap");
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
			System.out.println(fileAddress+" mapped from cache.");
		}else {
			verbose("map chache not exist");
			map = new HashMap<>();
			String string = getFileContentString(fileAddress, encodingName);
			Pattern p = Pattern.compile("<string[\\s]+?id[ ]?=[ ]?\"([a-zA-Z0-9_.'/, -]*?)\"[ ]?>\\s*?<text>([\\s\\S]*?)</text>\\s*?</string>");
			Matcher m = p.matcher(string);
			while (m.find()) {
				map.put(m.group(1), m.group(2));
			}
			ObjectOutputStream objectOutputStream = null;
			try {
				objectOutputStream = new ObjectOutputStream( new FileOutputStream(serializedMap));
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

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), encodeingName));
		writer.write(content);
		writer.flush();
		writer.close();
	}
	
	public static HashMap<String, String> getTextFileMap(String fileAddress) throws IOException, ClassNotFoundException {
		return getTextFileMap(fileAddress, "UTF-8");
	}

	public static String transToEN(String sentence) throws Exception {
		return transToTarget(sentence, "en");
	}

	public static String transToCN(String sentence) throws Exception {
		return transToTarget(sentence, "zh");
	}

	public static String transToTarget(String sentence, String targ) throws Exception {
		String translated = "";

		// when return the origin string
		if (sentence.matches("[\\s0-9,.!?]*?")) { // only numbers and punc
			return sentence;
		}
		if ("google".equals(transAPI)) {
			translated = GoogleWebTranslator.translate(sentence, targ);
		} else if ("bing".equals(transAPI)) {
			translated = BingWebTranslator.translate(sentence, targ);
		} else {
			try {
				translated = Dispatch.Instance(transAPI).Trans(oriLang, targ, sentence);
			} catch (Exception e) {
				System.err.println(e);
				if ("yandex".equals(transAPI) && e.getMessage().toLowerCase().contains("[\"text\"]")) {
					updateYandexKey();
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
		String keysString = getFileContentString("yandexKeys.xml");
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
	
	public static void verbose(String content) {
		if (verbose) {
			System.out.println(content);
		}
	}
}

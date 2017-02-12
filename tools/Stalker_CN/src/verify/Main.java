package verify;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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

public class Main {
	static int lackFileNum = 0;
	// static String localDirSeparater ="\\";
	static String localDirSeparater = "/";
	static long currentTimeMillis = System.currentTimeMillis();
	static String transAPI = "baidu";
	static String oriLang = "ru";
	static int sleepMilliSecond = 0;
	static ArrayList<String> keyList = new ArrayList<String>();
	static public String yandexKey = "";
	static int errorSleepMilliSecond = 2000;
	static boolean verbose = true;

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
		File chsDir = new File("D:\\SGM2.2_LostSoul_CNPack_Complete\\chs");
		File rusDir = new File("/Users/wzy/Desktop/HoMtext/rus");
		// File chsDir = new
		// File("/Users/wzy/Desktop/SGM2.2_LostSoul_CNPack_Complete/chs");
		ArrayList<String> finishedFiles = new ArrayList<String>();
		File transedDir = new File(rusDir.getPath() + localDirSeparater + "translated_" + transAPI);
		if (transedDir.exists()) {
			File[] transed = transedDir.listFiles();
			for (int i = 0; i < transed.length; i++) {
				finishedFiles.add(transed[i].getName());
			}
		}

		File[] rusXMLs = rusDir.listFiles();
		for (int i = 0; i < rusXMLs.length; i++) {
			if (rusXMLs[i].isFile() && !finishedFiles.contains(rusXMLs[i].getName())) {
				// File chs = new File(chsDir.getPath() + localDirSeparater +
				// rusXMLs[i].getName());
				// System.out.println(chs.getName()+" : ");
				// if (keyToKey(chs, rusXMLs[i])) {
				// return;
				// }
				// Thread.sleep(100);
				translateTextFile(rusXMLs[i]);
			}

		}
		System.out.println("lackFileNum:" + lackFileNum);
	}

	@SuppressWarnings("resource")
	public static boolean keyToKey(File chs, File rus) throws Exception {
		// String checkKeyword = "尼特罗";

		String chsString = getFileContentString(chs.getParent() + localDirSeparater + chs.getName(), "utf-8");
		String rusString = getFileContentString(chs.getParent() + localDirSeparater + rus.getName());
		String tmp = "";

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

	public static boolean isSentence(String str) {
		return (!str.matches("\\s*?")) && (!str.matches("[a-zA-Z0-9_-]*?"));
	}

	public static void translateGamePlayFile(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;

		String rusString = getFileContentString(rus.getParent() + localDirSeparater + rus.getName());

		String chsString = rusString;
		// clear BOM,delete annotation,anti-escape
		chsString = chsString.replaceAll("[\\s\\S]*?<?xml\\s", "<?xml ").replaceAll("<!--[\\s\\S]*?-->", "")
				.replaceAll("&apos;", "'").replaceAll("&quot;", "\"").replaceAll("&amp;", "&");

		// System.out.println(chsString);

		HashSet<String> sentences = new HashSet<>();
		Pattern p = Pattern.compile("<text[^u_/]*?>([\\s\\S]*?)</text>");
		Matcher m = p.matcher(chsString);
		int i = 0;
		while (m.find()) {
			System.out.println(m.group(1));
			if (isSentence(m.group(1))) {
				sentences.add(m.group(1));
				System.err.println(m.group(1));
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
				chsString = chsString.replace(string, transtedLine);
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

		chsString = chsString.replaceAll("encoding=\"(.*?)\"", "encoding=\"UTF-8\"").replaceAll("？", "?");

		// System.out.println(chsString);

		File resultDir = new File(rus.getParent() + localDirSeparater + "translated_" + transAPI);
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}

		File resultFile = new File(resultDir.getPath() + localDirSeparater + rus.getName());

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "utf-8"));
		writer.write(chsString);
		writer.close();

		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public static void translateTextFile(File rus) throws Exception {
		System.out.println("file \"" + rus.getName() + "\" started!");
		int transNum = 0;

		String rusString = getFileContentString(rus.getParent() + localDirSeparater + rus.getName());

		String chsString = rusString;
		// clear BOM,delete annotation,anti-escape
		chsString = chsString.replaceAll("[\\s\\S]*?<?xml\\s", "<?xml ").replaceAll("<!--[\\s\\S]*?-->", "")
				.replaceAll("&apos;", "'").replaceAll("&quot;", "\"").replaceAll("&amp;", "&");

		HashMap<String, String> sentences = new HashMap<>();
		Pattern p = Pattern.compile("<string id=\"([a-zA-Z0-9_-]*?)\">\\s*?<text>([\\s\\S]*?)</text>\\s*?</string>");
		Matcher m = p.matcher(chsString);
		int i = 0;
		while (m.find()) {
			sentences.put(m.group(1), m.group(2));
			i++;
		}
		System.out.println("find " + i + " sentences,map get " + sentences.size());

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

			// save the ACTION** and the COLOR
			// total regex
			// (?:[()"']?\$\$ACT[_A-Z0-9]*?\$\$[()"']?|%[a-z]\[[a-z0-9,]*?\]•?)
			// Pattern p1 = Pattern.compile(".?\\$\\$ACT.*?\\$\\$.?");
			Pattern p1 = Pattern
					.compile("(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?•?)");
			Matcher m1 = p1.matcher(oriLine);
			LinkedList<String> colorOrAction = new LinkedList<>();
			while (m1.find()) {
				colorOrAction.add(m1.group(0));
			}
			String[] pieces = string
					.split("(?:[()\"']?\\$\\$ACT[_A-Z0-9]*?\\$\\$[()\"']?|%[a-z]\\[[a-z0-9,]*?\\][\\s]*?•?)");
			// queue got

			String transtedLine = "";
			try {
				for (int j = 0; j < pieces.length; j++) {
					transtedLine = transtedLine + transToCN(pieces[j]);
					if (!colorOrAction.isEmpty()) {
						transtedLine = transtedLine + colorOrAction.get(0);
						colorOrAction.remove(0);
					}
					System.err.print("(." + (j + 1) + ")");
				}
			} catch (Exception e) {
				failFlag = true;
				System.out.println("");
				System.err.println(e);
				System.err.println(key);
				Thread.sleep(errorSleepMilliSecond);
				continue f1;
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

		chsString = chsString.replaceAll("encoding=\"(.*?)\"", "encoding=\"UTF-8\"")
				.replaceAll("<text></text>", "<text>返回空文本</text>").replaceAll("？", "?");

		File resultDir = new File(rus.getParent() + localDirSeparater + "translated_" + transAPI);
		if (!resultDir.exists()) {
			resultDir.mkdir();
		}

		File resultFile = new File(resultDir.getPath() + localDirSeparater + rus.getName());

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), "utf-8"));
		writer.write(chsString);
		writer.close();

		System.out.println("file \"" + rus.getName() + "\" done!");
	}

	public static String getFileContentString(String fileAddress) throws IOException {
		return getFileContentString(fileAddress, "windows-1251");
	}

	public static String getFileContentString(String fileAddress, String encodingName) throws IOException {
		File file = new File(fileAddress);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), encodingName));
		String string = "";
		String tmp = "";
		for (; (tmp = reader.readLine()) != null;) {
			if (!tmp.matches("[\r\n]*?")) {
				string = string + tmp + "\n";
			}
		}
		reader.close();
		return string;
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

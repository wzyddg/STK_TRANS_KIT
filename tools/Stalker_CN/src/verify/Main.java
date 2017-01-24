package verify;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.lsj.trans.Dispatch;;

public class Main {
	static int lackFileNum = 0;

	public static void main(String[] args) throws Exception {
		Class.forName("com.lsj.trans.GoogleDispatch");	
		// TODO Auto-generated method stub
		for (int i = 0; i < args.length; i++) {
			System.out.println(args[i]);
		}
		File rusDir = new File("D:\\SGM2.2_LostSoul_CNPack_Complete\\rus");
		File chsDir = new File("D:\\SGM2.2_LostSoul_CNPack_Complete\\chs");
		File[] rusXMLs = rusDir.listFiles();
		for (int i = 0; i < rusXMLs.length; i++) {
			if (rusXMLs[i].isFile()) {
				File chs = new File(chsDir.getPath()+"\\"+rusXMLs[i].getName());
//				System.out.println(chs.getName()+" : ");
//				if (keyToKey(chs, rusXMLs[i])) {
//					return;
//				}
//				Thread.sleep(100);
			}
			
		}
//		System.out.println("Ну даже не знаю, можно ли назвать это работой. В общем, тут неподалёку от нас отряд псевдособак поселилс");st_ogsm_notepad_anomalies.xml
//		goThroughLine(new File("D:\\S.T.A.L.K.E.R. Wind of Time v1.0\\gamedata\\configs\\text\\rus\\st_ogsm_notepad_anomalies.xml"));
//		goThroughLine(new File("D:\\S.T.A.L.K.E.R. Wind of Time v1.0\\gamedata\\configs\\text\\rus\\st_dialogs_nii.xml"));
//		goThroughLine(new File("D:\\S.T.A.L.K.E.R. Wind of Time v1.0\\gamedata\\configs\\text\\rus\\st_dialogs_nii.xml"));
//		goThroughLine(new File("D:\\S.T.A.L.K.E.R. Wind of Time v1.0\\gamedata\\configs\\text\\rus\\st_dialogs_nii.xml"));
//		goThroughLine(new File("D:\\S.T.A.L.K.E.R. Wind of Time v1.0\\gamedata\\configs\\text\\rus\\st_dialogs_nii.xml"));
//		goThroughLine(new File("D:\\S.T.A.L.K.E.R. Wind of Time v1.0\\gamedata\\configs\\text\\rus\\st_dialogs_nii.xml"));
		System.out.println("lackFileNum:"+lackFileNum);
	}

	@SuppressWarnings("resource")
	public static boolean keyToKey(File chs,File rus) throws Exception {
		String checkKeyword = "尼特罗";
		
		BufferedReader chsReader = new BufferedReader(new FileReader(chs));
        String chsString = "";
        BufferedReader rusReader = new BufferedReader(new FileReader(rus));
        String rusString = "";
        String tmp = "";
        while ((tmp = chsReader.readLine()) != null) {
            chsString = chsString + tmp;
        }
        chsReader.close();
        if(chsString.contains(checkKeyword)){
        	throw new Exception(checkKeyword+" exists in "+chs.getName());
        }
        while ((tmp = rusReader.readLine()) != null) {
            rusString = rusString + tmp;
        }
        rusReader.close();
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
				throw new Exception("chsID: "+found+" duplicated in "+chs.getName());
			}
        	chsIDs.add(found);
        	rusIDs.remove(m.group(1));            
        }
        m = p.matcher(rusString);
        while (m.find()) {
        	chsIDs.remove(m.group(1));            
        }
        
		for (Iterator<String> iterator = rusIDs.iterator(); iterator.hasNext();) {
			if(!flag){
				System.out.println(chs.getName()+":");
				flag=true;
				lackFileNum++;
			}
			String string = (String) iterator.next();
			System.err.println(string);
		}
		for (Iterator<String> iterator = chsIDs.iterator(); iterator.hasNext();) {
			if(!flag){
				System.out.println(rus.getName()+":");
				flag=true;
			}
			String string = iterator.next();
			System.out.println(string);
		}
		
		return flag;
	}
	
	public static void goThroughLine(File rus) throws Exception {
		BufferedReader rusReader = new BufferedReader(new InputStreamReader(new FileInputStream(rus), "windows-1251"));
        String rusString = "";
        String tmp = "";
        
        for(int i =0;(tmp = rusReader.readLine()) != null;i++) {
        	System.out.println(""+i+" : "+transToCN(tmp));
//        	Thread.sleep(100);
            rusString = rusString + tmp;
        }
        
        rusReader.close();
	}
	
	public static String transToEN(String ori) throws Exception {
		return Dispatch.Instance("google").Trans("ru", "en", ori);
	}
	
	public static String transToCN(String ori) throws Exception {
		return Dispatch.Instance("google").Trans("ru", "zh", ori);
	}

//	public static boolean key2key(File chs,File rus) throws ParserConfigurationException, SAXException, IOException, InterruptedException {
//		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
//		DocumentBuilder builder;
//		builder = builderFactory.newDocumentBuilder();
//		Document chsDoc = builder.parse(chs); 
//		NodeList chsStrings = chsDoc.getElementsByTagName("string");
//		Document rusDoc = builder.parse(rus); 
//		NodeList rusStrings = rusDoc.getElementsByTagName("string");
//		
//		ArrayList<String> chsIDs = new ArrayList<>();
//		ArrayList<String> rusIDs = new ArrayList<>();
//		
//		boolean flag = false;
//
//		for (int i = 0; i < rusStrings.getLength(); i++){
//			Element rusE = (Element) rusStrings.item(i);
//			rusIDs.add(rusE.getAttribute("id"));
//		}
//		for (int i = 0; i < chsStrings.getLength(); i++) {
//			Element chsE = (Element) chsStrings.item(i);
////			if(!rusIDs.contains(chsE.getAttribute("id"))){
////				System.out.println(chsE.getAttribute("id"));
////			}
//			rusIDs.remove(chsE.getAttribute("id"));
//		}
//		for (Iterator<String> iterator = rusIDs.iterator(); iterator.hasNext();) {
//			if(!flag){
//				System.out.println(chs.getName()+":");
//				flag=true;
//				lackFileNum++;
//			}
//			String string = iterator.next();
//			System.err.println(string);
//		}
//		for (Iterator<String> iterator = chsIDs.iterator(); iterator.hasNext();) {
//			if(!flag){
//				System.out.println(rus.getName()+":");
//				flag=true;
//			}
//			String string = iterator.next();
//			System.out.println(string);
//		}
//		Thread.sleep(20);
//		return flag;
//	}
	
	public static boolean goThroughIDs(File chs) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance(); 
		DocumentBuilder builder;
		builder = builderFactory.newDocumentBuilder();
		Document chsDoc = builder.parse(chs); 
		NodeList chsStrings = chsDoc.getElementsByTagName("string");
		
		//show what in rus not in chs
		for (int i = 0; i < chsStrings.getLength(); i++){
			Element chsE = (Element) chsStrings.item(i);
			System.out.println(chsE.getAttribute("id"));
		}
		
		return true;
	}
}

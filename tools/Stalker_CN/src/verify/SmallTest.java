package verify;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lsj.trans.Dispatch;
import com.sun.java_cup.internal.runtime.Scanner;

public class SmallTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		MainTrans.yandexKey = "trnsl.1.1.20170203T170914Z.3aa140986a456c8a.5a30bea15be7883744cf1a6e26867d5e80444960";
		Class.forName("com.lsj.trans.YandexDispatch");
		Class.forName("com.lsj.trans.BingNeuralDispatch");
		
		MainTrans.sleepMilliSecond = 0;
		String[] strings = {
//				"Жаба\\nМессер, в ангаре в разрушенных складских помещениях есть один специфический артефакт. Принеси мне его."
		};
		MainTrans.translateStringArray(strings);

		String chsString = "\"%c[255,160,160,160]\"..\"ОЛЕГ ТАНГО:\"..\"\\n\"..\"%c[255,255,128,128]Ну, что мужики, кто пойдёт в нелёгкий путь! Пешком очень далеко и один шанс - это выбить вертуху у военсталкеров. Я иду точно. Нам кровью и потом достались эти деньги...\"..\"\"..\"\\n\"";
		Pattern p = Pattern.compile("\"([АаБбВвГгДдЕеЁёЖжЗзИиЙйКкЛлМмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщъЫыьЭэЮюЯя ,.?!-_QWERTYUIOPLKJHGFDSAZXCVBNMqwertyuiopasdfghjklzxcvbnm0123456789\\\\]*?)(?=\")");
		Matcher m = p.matcher(chsString);
		while (m.find()) {
			System.out.println(m.group(1));
		}
	}

	static void sss() {
		try {
			throw new Exception();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("kk");
		}
		System.out.println("ll");
	}

}

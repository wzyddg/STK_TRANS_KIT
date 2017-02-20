package com.lsj.trans;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import org.apache.http.entity.ContentType;
import com.lsj.http.HttpPostParams;
import net.sf.json.JSONObject;
import verify.MainTrans;

public class BingNeuralDispatch extends Dispatch {
	static {
		BingNeuralDispatch dispatch = new BingNeuralDispatch();
		classMap.put("bingn", dispatch);
		classMap.put("BingN", dispatch);
	}

	private BingNeuralDispatch() {
		langMap.put("en", "en");
		langMap.put("zh", "zh-Hans");
		langMap.put("ru", "ru");
		langMap.put("auto", "");
	}

	@Override
	public String Trans(String from, String targ, String query) throws Exception {
		if (query.matches("[\\s]*?")) {
			return "";
		}
		if (query.contains("\n")||query.contains("\r")||query.contains("\\n")) {
			String[] paragraphs = query.split("[\\n\\r]|\\\\n");
			String tmp = "";
			for (int i = 0; i < paragraphs.length; i++) {
				tmp = tmp + Trans(from, targ, paragraphs[i]);
				if (i<paragraphs.length-1) {
					tmp = tmp + "\\n";
				}
			}
			return tmp;
		}
		String[] paragraphs = query.split("[.]");
		String tmp = "";
		LinkedList<String> posts = new LinkedList<>();
		for (int i = 0; i < paragraphs.length; i++) {
			if (tmp.length() + paragraphs[i].length() > 1000) {
				posts.add(tmp.substring(1));
				tmp = "";
			}
			tmp = tmp + "." + paragraphs[i];
			if (i == paragraphs.length - 1) {
				tmp = tmp.substring(1);
				posts.add(tmp);
			}
		}
		// send queue got
		String all = "";
		MainTrans.verbose("seperated to "+posts.size()+"pieces.");
		for (; !posts.isEmpty();) {
			String string = posts.get(0);
			HttpPostParams params = new HttpPostParams();
			params.put("SourceLanguage", langMap.get(from)).put("TargetLanguage", langMap.get(targ)).put("Text",
					string);
			try {
				String jsonString = params.Send("https://translator.microsoft.com/neural/api/translator/translate",
						ContentType.APPLICATION_JSON);
				tmp = ParseString(jsonString);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.err.println(e);
				continue;
			}
			all = all + tmp + ".";
			posts.remove(0);
		}
		return all;
	}

	private String ParseString(String jsonString) throws UnsupportedEncodingException {
		String result = "";
		try {
			JSONObject jsonObject = JSONObject.fromObject(jsonString);
			result = jsonObject.getString("resultNMT");
		} catch (Exception e) {
			MainTrans.verbose("jsonString : "+jsonString+" over");
			throw e;
			// TODO: handle exception
		}
		return new String(result);
	}
}

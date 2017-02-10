package com.lsj.trans;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.apache.http.entity.ContentType;

import com.lsj.http.HttpPostParams;

import net.sf.json.JSONObject;
import verify.Main;

public class BingNeuralDispatch extends Dispatch{
	static{
		BingNeuralDispatch dispatch = new BingNeuralDispatch();
		classMap.put("bing", dispatch);
		classMap.put("Bing", dispatch);
	}

	private BingNeuralDispatch(){
		langMap.put("en", "en");
		langMap.put("zh", "zh-Hans");
		langMap.put("ru", "ru");
	}

	@Override
	public String Trans(String from, String targ, String query) throws Exception {
		String[] paragraphs = query.split("[\n.]");
		String tmp = "";
		LinkedList<String> posts = new LinkedList<>();
		for (int i = 0; i < paragraphs.length; i++) {
			if (tmp.length()+paragraphs[i].length()>1000) {
				posts.add(tmp);
				tmp = "";
			}
			tmp = tmp+"\n"+paragraphs[i];
			if (i==paragraphs.length-1) {
				tmp = tmp.substring(1);
				posts.add(tmp);
			}
		}
//		System.out.println(posts.size());
		//send queue got
		String all = "";
		for (;!posts.isEmpty();) {
			String string = posts.get(0);
			HttpPostParams params = new HttpPostParams();
			params.put("SourceLanguage", langMap.get(from))
					.put("TargetLanguage", langMap.get(targ))
					.put("Text", string);
			String jsonString = params.Send("https://translator.microsoft.com/neural/api/translator/translate",ContentType.APPLICATION_JSON);
			tmp = ParseString(jsonString);
			all = all+"\n"+tmp;
			posts.remove(0);
		}
		all = all.substring(1);
		
//		HttpParams params = new HttpPostParams()
////				.put("key", key)
////				.put("lang", from+"-"+targ)
//				.put("text", query);
//		String jsonString = params.Send("https://translate.yandex.net/api/v1.5/tr.json/translate?key="+Main.yandexKey+"&lang="+from+"-"+targ);
//		tmp = ParseString(jsonString);
		return all;
	}

	private String ParseString(String jsonString) throws UnsupportedEncodingException{
		System.err.println(jsonString);
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		String result = jsonObject.getString("resultNMT");
		return new String(result);
	}
}

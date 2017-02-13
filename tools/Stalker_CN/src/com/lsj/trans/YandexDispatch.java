package com.lsj.trans;

import java.util.LinkedList;

import com.lsj.http.HttpParams;
import com.lsj.http.HttpPostParams;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import verify.Main;

public class YandexDispatch extends Dispatch {

	static {
		YandexDispatch dispatch = new YandexDispatch();
		classMap.put("yandex", dispatch);
		classMap.put("Yandex", dispatch);
	}

	private YandexDispatch() {
		langMap.put("en", "en");
		langMap.put("zh", "zh");
		langMap.put("ru", "ru");
	}

	@Override
	public String Trans(String from, String targ, String query) throws Exception {
		String[] paragraphs = query.split("[\n.\r]|\\n");
		String tmp = "";
		LinkedList<String> posts = new LinkedList<>();
		for (int i = 0; i < paragraphs.length; i++) {
			if (tmp.length() + paragraphs[i].length() > 8000) {
				posts.add(tmp.substring(1));
				tmp = "";
			}
			tmp = tmp + "." + paragraphs[i];
			if (i == paragraphs.length - 1) {
				tmp = tmp.substring(1);
				posts.add(tmp);
			}
		}
		// System.out.println(posts.size());
		// send queue got
		String all = "";
		for (; !posts.isEmpty();) {
			String string = posts.get(0);
			HttpParams params = new HttpPostParams()
					// .put("key", key)
					// .put("lang", from+"-"+targ)
					.put("text", string);
			String jsonString = params.Send("https://translate.yandex.net/api/v1.5/tr.json/translate?key="
					+ Main.yandexKey + "&lang=" + from + "-" + targ);
			tmp = ParseString(jsonString);
			all = all + "." + tmp;
			posts.remove(0);
		}
		all = all.substring(1);

		// HttpParams params = new HttpPostParams()
		//// .put("key", key)
		//// .put("lang", from+"-"+targ)
		// .put("text", query);
		// String jsonString =
		// params.Send("https://translate.yandex.net/api/v1.5/tr.json/translate?key="+Main.yandexKey+"&lang="+from+"-"+targ);
		// tmp = ParseString(jsonString);
		return all;
	}

	private String ParseString(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray segments = jsonObject.getJSONArray("text");
		String result = segments.getString(0);
		return new String(result);
	}
}

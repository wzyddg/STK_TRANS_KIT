package com.lsj.trans;

import java.net.URLEncoder;

import com.lsj.http.HttpGetParams;
import com.lsj.http.HttpParams;
import com.lsj.http.HttpPostParams;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import verify.Main;

public class YandexDispatch extends Dispatch {
	
	static{
		YandexDispatch dispatch = new YandexDispatch();
		classMap.put("yandex", dispatch);
		classMap.put("Yandex", dispatch);
	}

	private YandexDispatch(){
		langMap.put("en", "en");
		langMap.put("zh", "zh");
		langMap.put("ru", "ru");
	}
	
	@Override
	public String Trans(String from, String targ, String query) throws Exception {
		// TODO Auto-generated method stub
		HttpParams params = new HttpGetParams();
//				.put("key", key)
//				.put("lang", from+"-"+targ)
//				.put("text", URLEncoder.encode(query, "utf-8"));
		
		String jsonString = params.Send("https://translate.yandex.net/api/v1.5/tr.json/translate?key="+Main.yandexKey+"&lang="+from+"-"+targ+"&text="+URLEncoder.encode(query, "utf-8"));
		return ParseString(jsonString);
	}

	private String ParseString(String jsonString){
		System.out.println(jsonString);
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		JSONArray segments = jsonObject.getJSONArray("text");
		String result = segments.getString(0);
		return new String(result);
	}
}

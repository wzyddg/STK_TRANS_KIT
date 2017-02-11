package com.lsj.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.JsonObject;

public class HttpPostParams extends AbstractHttpParams {
	@Override
	public String Send(String base) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost request = new HttpPost(base);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			String value = params.get(key);
			formparams.add(new BasicNameValuePair(key, value));
		}
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
		CloseableHttpResponse response = httpClient.execute(request);
		return ReadInputStream(response.getEntity().getContent());
	}

	public String Send(String base, ContentType contentType) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost request = new HttpPost(base);
		if (ContentType.APPLICATION_JSON.equals(contentType)) {
			JsonObject jObject = new JsonObject();
			for (String key : params.keySet()) {
				String value = params.get(key);
				jObject.addProperty(key, value);
			}
			request.setHeader("Content-Type", contentType.toString());
			request.setEntity(new StringEntity(jObject.toString(), "utf-8"));
		} else {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			for (String key : params.keySet()) {
				String value = params.get(key);
				formparams.add(new BasicNameValuePair(key, value));
			}
			request.setHeader("Content-Type", "application/x-www-form-urlencoded");
			request.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
		}
		CloseableHttpResponse response = httpClient.execute(request);
		return ReadInputStream(response.getEntity().getContent());
	}
}

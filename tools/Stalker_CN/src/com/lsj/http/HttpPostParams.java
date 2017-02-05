package com.lsj.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class HttpPostParams extends AbstractHttpParams{
	@Override
	public String Send(String base) throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			String value = params.get(key);
			formparams.add(new BasicNameValuePair(key, value));
		}
		HttpPost request = new HttpPost(base);
		request.setHeader("Content-Type", "application/x-www-form-urlencoded");
		request.setEntity(new UrlEncodedFormEntity(formparams, "UTF-8"));
		CloseableHttpResponse response = httpClient.execute(request);
		return ReadInputStream(response.getEntity().getContent());
	}
}

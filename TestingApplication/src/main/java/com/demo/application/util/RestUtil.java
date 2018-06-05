package com.demo.application.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestUtil {

	private static Logger logger = LoggerFactory.getLogger(RestUtil.class);

	public static JSONObject callRest(String url, String entity, String method, String user, String pass) {

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpRequestBase httpRequestBase = null;
		HttpResponse httpResponse = null;
		StringEntity input = null;
		String json = null;
		JSONObject obj = null;
		if (!ServicesUtil.isEmpty(url)) {
			if (method.equalsIgnoreCase("GET")) {
				httpRequestBase = new HttpGet(url);
			} else if (method.equalsIgnoreCase("POST")) {
				httpRequestBase = new HttpPost(url);
				try {
					input = new StringEntity(entity);
					input.setContentType("application/json");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				((HttpPost) httpRequestBase).setEntity(input);
			}
			httpRequestBase.addHeader("accept", "application/json");
			if (!ServicesUtil.isEmpty(user) && !ServicesUtil.isEmpty(pass)) {
				String userPassword = user + ":" + pass;
				byte[] encodeBase64 = Base64.encodeBase64(userPassword.getBytes());
				httpRequestBase.addHeader("Authorization", "BASIC " + new String(encodeBase64));
			}
			try {
				httpResponse = httpClient.execute(httpRequestBase);
				json = EntityUtils.toString(httpResponse.getEntity());
			} catch (IOException e) {
				logger.error("IOException : " + e);
			}

			try {
				obj = new JSONObject(json);
			} catch (JSONException e) {
				logger.error("JSONException : " + e);
			}

			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("Closing HttpClient Exception : " + e);
			}

		}
		return obj;
	}

	public static void main(String[] args) {
//		System.out.println(RestUtil.callRest("http://localhost:8080/AppDownload/app/testing/getUser?id=1", null, "GET", null, null));
		System.out.println(RestUtil.callRest("https://my87.geotab.com/apiv1", "{\"method\":\"Get\",\"params\":{\"credentials\":{\"database\":\"murphy_oil\",\"password\":\"$tr3amlin3\",\"userName\":\"SVC_IOP@murphyoilcorp.com\"},\"typeName\":\"Zone\"}}", "POST", null, null)); 
	}
}

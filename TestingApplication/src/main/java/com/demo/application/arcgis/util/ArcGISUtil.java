package com.demo.application.arcgis.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.demo.application.dto.ArcGISAccessTokenDto;
import com.demo.application.geotab.Coordinates;
import com.demo.application.geotab.GeoTabConstants;
import com.demo.application.util.ServicesUtil;

public class ArcGISUtil {

	public static Double getRoadDistance(Coordinates coordOne, Coordinates coordTwo) {
		
		String authToken = null;
		try {
			authToken = (String) ServicesUtil.getProperty("arcgis.token");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

		//		String accessToken = ArcGISUtil.getAccessToken();
		//		String accessToken = "w7tl7Ij2a1d3ACQCm41optp5SKF6W-_pHJ8VMvNYt8pH68O81ZGpJDZabfO3jFnNGH6P5Z3lLUxrfEOxT7EmcTrP6H98-XH1m9-q5_T0T-VNRjxOkvDPz9HzVVV7iraz5TQQplfu_opBD1PoHMoYTA..";
		String serviceURL = GeoTabConstants.ARCGIS_SERVICE_URL;
		String stops = "{ \"type\":\"features\", \"features\": [ { \"geometry\": { \"x\": "+coordOne.getLongitude()+", \"y\": "+coordOne.getLatitude()+", } }, { \"geometry\": { \"x\": "+coordTwo.getLongitude()+", \"y\": "+coordTwo.getLatitude()+", } } ] }";

		JSONObject arcGISResponse = ArcGISUtil.callArcGISService(serviceURL, authToken, stops);
		JSONArray inArray = arcGISResponse.getJSONObject("routes").getJSONArray("features");
		JSONObject inObject = inArray.getJSONObject(0).getJSONObject("attributes");
		//		System.out.println(inObject.get("Total_Kilometers"));

		//		System.out.println(inObject.get("attributes"));

		return (Double) inObject.get("Total_Kilometers");
	}

	public static JSONObject callArcGISService(String serviceURL, String accessToken, String stops) {

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = null;
		HttpResponse httpResponse = null;
		StringEntity input = null;
		String json  = "";
		JSONObject jsonObject = null;

		if(!ServicesUtil.isEmpty(serviceURL)) {
			httpPost = new HttpPost(serviceURL);

			httpPost.addHeader("Content-Type", GeoTabConstants.CONTENT_TYPE_URL_FORM_ENCODED);
			String entity = "f=json&token="+accessToken+"&stops="+stops+"";

			try {
				input = new StringEntity(entity);
				input.setContentType(GeoTabConstants.CONTENT_TYPE_URL_FORM_ENCODED);
				httpPost.setEntity(input);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			try {
				httpResponse = httpClient.execute(httpPost);
				json = EntityUtils.toString(httpResponse.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			if(!ServicesUtil.isEmpty(json)) {
				jsonObject = new JSONObject(json);
				if(verifyResponse(json)) {
					return callArcGISService(serviceURL, accessToken, stops);
				}
			}
		}
		return jsonObject;
	}

	public static ArcGISAccessTokenDto getAccessToken() {

		ArcGISAccessTokenDto accessTokenDto = new ArcGISAccessTokenDto(); 

		String serviceURL = GeoTabConstants.ARCGIS_SERVICE_TOKEN_URL;
		String clientId = GeoTabConstants.ARCGIS_CLIENT_ID;
		String clientSecret = GeoTabConstants.ARCGIS_CLIENT_SECRET;

		CloseableHttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost httpPost = null;
		HttpResponse httpResponse = null;
		StringEntity input = null;
		String json  = "";

		if(!ServicesUtil.isEmpty(serviceURL)) {
			httpPost = new HttpPost(serviceURL);

			httpPost.addHeader("Content-Type", GeoTabConstants.CONTENT_TYPE_URL_FORM_ENCODED);

			String entity = "client_id="+clientId+"&client_secret="+clientSecret+"&grant_type=client_credentials&expiration=1209600";

			try {
				input = new StringEntity(entity);
				input.setContentType(GeoTabConstants.CONTENT_TYPE_URL_FORM_ENCODED);
				httpPost.setEntity(input);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			try {
				httpResponse = httpClient.execute(httpPost);
				json = EntityUtils.toString(httpResponse.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!ServicesUtil.isEmpty(json)) {
				JSONObject jsonObject = new JSONObject(json);
				accessTokenDto.setAccessToken(jsonObject.getString("access_token"));
				accessTokenDto.setValidity(jsonObject.getInt("expires_in"));
			}
		}
		return accessTokenDto;
	}

	public static void main(String[] args) throws ClientProtocolException, IOException {
//		System.out.println(ArcGISUtil.getAccessToken());
		System.out.println(ArcGISUtil.getRoadDistance(new Coordinates(12.936027, 77.691249), new Coordinates(12.956518, 77.701054)));
//		ArcGISUtil.setProperty();

	}

	private static Boolean verifyResponse(String response) {
		if(response.contains("error")) {
			JSONObject responseObject = new JSONObject(response);
			JSONObject errorResponse = responseObject.getJSONObject("error");
			if(!ServicesUtil.isEmpty(errorResponse)) {
				if(errorResponse.getInt("code") == 498) {
					ServicesUtil.refreshArcGISToken();
					return true;
				}
			}
		}
		return false;
	}

}

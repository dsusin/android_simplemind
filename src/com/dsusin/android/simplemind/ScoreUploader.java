package com.dsusin.android.simplemind;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ScoreUploader {
	public static final String TAG = "ScoreUploader";
	public static final String API_URL = "http://10.0.2.2:5000/simplemind";
	public static final String API_URL_HTTPS = "https://10.0.2.2/simplemind";

	byte[] getPostResultBytes(String user, int score) throws IOException,
			JSONException {
		//URL url=new URL(API_URL+"/post");
		URL url = new URL(API_URL_HTTPS + "/post");

		HttpURLConnection conn = null;
		if (url.getProtocol().toLowerCase().equals("https")) {
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) url
					.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			conn = https;
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		//HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");

		JSONObject json = new JSONObject();
		json.put("user", user);
		json.put("score", score);
		String jsonString = json.toString();

		try {
			conn.getOutputStream().write(jsonString.getBytes());

			int response = conn.getResponseCode();
			if (response != HttpURLConnection.HTTP_OK) {
				return null;
			}
			try {
				InputStream in = conn.getInputStream();
				ByteArrayOutputStream out = new ByteArrayOutputStream();

				int bytesRead = 0;
				byte[] buffer = new byte[1024];
				while ((bytesRead = in.read(buffer)) > 0) {
					out.write(buffer, 0, bytesRead);
				}
				out.close();
				return out.toByteArray();
			} catch (SSLHandshakeException she) {
				Log.e(TAG, she.getMessage());
			}
		} finally {
			conn.disconnect();
		}
		return null;
	}

	public String submitResult(String user, int score) throws IOException,
			JSONException {
		return new String(getPostResultBytes(user, score));
	}

	// NOT IN PRODUCTION
	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

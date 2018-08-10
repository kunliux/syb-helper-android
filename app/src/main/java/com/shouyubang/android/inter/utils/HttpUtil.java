/**
 *
 */
package com.shouyubang.android.inter.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil
{
	private static final String TAG = "HttpUtil";

    private static final int TIME_OUT = 5000;// 超时时间，默认为10s

	private static final MediaType JSON
			= MediaType.parse("application/json; charset=utf-8");

	private static byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(TIME_OUT);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException(connection.getResponseMessage() +
						": with " +
						urlSpec);
			}
			Log.i(TAG, "Get " + urlSpec);
			int bytesRead;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		} finally {
			connection.disconnect();
		}
	}

	public static String getUrlString(String urlSpec) throws IOException {
		String result = new String(getUrlBytes(urlSpec));
        Log.i(TAG, "Received JSON: " + result);
		return result;
	}

	public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					connection.setDoOutput(true);
					InputStream in = connection.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					StringBuilder response = new StringBuilder();
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line);
					}
					if (listener != null) {
						// 回调onFinish()方法
						listener.onFinish(response.toString());
					}
				} catch (Exception e) {
					if (listener != null) {
						// 回调onError()方法
						listener.onError(e);
					}
				} finally {
					if (connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}

	public static String get(final String url) throws IOException{
		Log.i(TAG, "Request: " + url);
		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5, TimeUnit.SECONDS)
				.build();
		Request request = new Request.Builder()
				.url(url)
				.build();
		String responseData = "";
		try {
			Response response = client.newCall(request).execute();
			responseData = response.body().string();
			Log.d(TAG, responseData);
			response.body().close();
		} catch (IOException e) {
			Log.e(TAG, "Http Get Method Exception:", e);
		}
		return responseData;
	}

	public static String post(String url, String json) throws IOException{
        Log.i(TAG, "Request: " + url);
		OkHttpClient client = new OkHttpClient.Builder()
				.connectTimeout(5, TimeUnit.SECONDS)
				.readTimeout(5, TimeUnit.SECONDS)
				.build();
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();
		String responseData = "";
		try {
			Response response = client.newCall(request).execute();
			responseData = response.body().string();
			Log.d(TAG, responseData);
			response.body().close();
		} catch (IOException e) {
			Log.e(TAG, "Http Post Method Exception:", e);
		}
		return responseData;
	}
}

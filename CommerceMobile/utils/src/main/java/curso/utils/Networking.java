package curso.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.util.Log;

public class Networking {

	public static String getHttpRequet(String urlAndress) throws IOException {
		try {
			URL url = new URL(urlAndress);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();

			int status = connection.getResponseCode();
			InputStream inputStream = connection.getInputStream();
			if (inputStream == null)
				return null;

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					inputStream));

			String line;
			StringBuilder sb = new StringBuilder();

			// Esta dando falta de memória nesta parte quando a string é muito
			// grande
			while ((line = rd.readLine()) != null)
				sb.append(line);

			rd.close();
			return sb.toString();
		} catch (OutOfMemoryError e1) {
			return "Erro";
		}
	}

	public static String performPostCall(String requestURL,
			Map<String, String> postDataParams) throws IOException {

		URL url;
		String response = "";
		// String TAG = null;
		try {
			url = new URL(requestURL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Type", "application/json");

			JSONObject root = new JSONObject();

			for (Map.Entry<String, String> pair : postDataParams.entrySet()) {
				root.put(pair.getKey(), pair.getValue());
			}
			// Log.e(TAG, "2 - root : " + root.toString());

			String str = root.toString();
			byte[] outputBytes = str.getBytes("UTF-8");
			OutputStream os = conn.getOutputStream();
			os.write(outputBytes);

			int responseCode = conn.getResponseCode();
			// Log.e(TAG, "3 - responseCode : " + responseCode);

			if (responseCode == HttpsURLConnection.HTTP_OK) {
				// Log.e(TAG, "4 - HTTP_OK");
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = br.readLine()) != null) {
					response += line;
				}
			} else {
				// Log.e(TAG, "4 - False - HTTP_OK");
				response = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public static String enviarImagem(String requestURL,
			Map<String, byte[]> postDataParams) throws IOException {

		URL url;
		String response = "";
		String boundary = "*****";
		 String TAG = null;
		try {
			url = new URL(requestURL);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);

			byte[] byteArrayImagem = null;
			for (Map.Entry<String, byte[]> pair : postDataParams.entrySet()) {
				byteArrayImagem = pair.getValue();
			}
			Log.e(TAG, "2 - byte : " + byteArrayImagem);
			OutputStream os = conn.getOutputStream();
			os.write(byteArrayImagem);

			int responseCode = conn.getResponseCode();
			 Log.e(TAG, "3 - responseCode : " + responseCode);
			if (responseCode == HttpsURLConnection.HTTP_OK) {
				 Log.e(TAG, "4 - HTTP_OK");
				String line;
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				while ((line = br.readLine()) != null) {
					response += line;
				}
			} else {
				 Log.e(TAG, "4 - False - HTTP_OK");
				response = "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response;
	}

	public static String postHttpRequest(String urlAndress, String content)
			throws IOException {
		URL url = new URL(urlAndress);
		URLConnection connection = url.openConnection();
		// vai mandar conteudo nesta conexao
		connection.setDoOutput(true);
		// vai receber conteudo nesta conexao
		connection.setDoInput(true);
		// informando o campo que esta mandando para o servidor do tipo json
		connection.setRequestProperty("Content-type", "application/json");

		DataOutputStream ostream = new DataOutputStream(
				connection.getOutputStream());
		// grava o texto na conexao
		ostream.writeBytes(content);
		// envia o texto na conexao
		ostream.flush();
		ostream.close();

		// le o que tem no buffer e retorna os dados encontrados
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String line;
		StringBuilder sb = new StringBuilder();

		while ((line = rd.readLine()) != null)
			sb.append(line);

		rd.close();
		return sb.toString();
	}

	public static boolean deleteHttpRequest(String urlAndress)
			throws IOException {
		URL url = new URL(urlAndress);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("DELETE");
		int resposta = connection.getResponseCode();
		return (resposta == 200 ? true : false);
	}

}

package com.oracle.oradocs.folders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import jdk.nashorn.internal.parser.JSONParser;

public class OraDocsFolders {

	private final String URL = "https://oradocs-corp.documents.us2.oraclecloud.com/documents/api/1.2/folders/F30CFB1F2E8D65077F6A5B0BF6C3FF17C1177A968060/items";
	private final String USER_AGENT = "Mozilla/5.0";

	public void getFolders() throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL);

		// add request header
		request.addHeader("authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6InUwY1BSU3dCYkE3V1BkZGR1MGo2QlBfRXZxVSIsImtpZCI6ImNvcnAuY2VydCJ9.eyJzdWIiOiJzaGlraGFyLm1pc2hyYUBvcmFjbGUuY29tIiwib3JhY2xlLm9hdXRoLnVzZXJfb3JpZ2luX2lkX3R5cGUiOiJMREFQX1VJRCIsIm9yYWNsZS5vYXV0aC51c2VyX29yaWdpbl9pZCI6InNoaWtoYXIubWlzaHJhQG9yYWNsZS5jb20iLCJpc3MiOiJjb3JwIiwib3JhY2xlLm9hdXRoLnN2Y19wX24iOiJjb3JwU2VydmljZVByb2ZpbGUiLCJpYXQiOjE1MDg3NzM3MjcsIm9yYWNsZS5vYXV0aC5wcm4uaWRfdHlwZSI6IkxEQVBfVUlEIiwib3JhY2xlLm9hdXRoLnRrX2NvbnRleHQiOiJyZXNvdXJjZV9hY2Nlc3NfdGsiLCJleHAiOjE1MDkzNzg1MjYsImF1ZCI6WyJodHRwczovL29yYWRvY3MtY29ycC5kb2N1bWVudHMudXMyLm9yYWNsZWNsb3VkLmNvbTo0NDMvZG9jdW1lbnRzIl0sIm9yYWNsZS5vYXV0aC5zdWIuaWRfdHlwZSI6IkxEQVBfVUlEIiwicHJuIjoic2hpa2hhci5taXNocmFAb3JhY2xlLmNvbSIsImp0aSI6ImU3ZDdiZGQxLTQ1YjQtNDI5Yi04Mjg4LTU0NzgyZTYxZDUyNSIsIm9yYWNsZS5vYXV0aC5jbGllbnRfb3JpZ2luX2lkIjoiYmYyMDYxNTMtNWE0ZS00MjEyLTk5NTYtMjVhNmM0NDRlNDAyIiwib3JhY2xlLm9hdXRoLnNjb3BlIjoiaHR0cHM6Ly9vcmFkb2NzLWNvcnAuZG9jdW1lbnRzLnVzMi5vcmFjbGVjbG91ZC5jb206NDQzL2RvY3VtZW50cyIsInVzZXIudGVuYW50Lm5hbWUiOiJjb3JwIiwib3JhY2xlLm9hdXRoLmlkX2RfaWQiOiI2NjY4ODgxNzI5ODEyNTI1NCJ9.KMPTpmcxabZ9D-SUVGWgTSMGaA-eTDZCNb_CVXr7suFyFbWcHlK89oNrBpsdu1-9jWAwh7nA5wUjXor9Bgco0WYWZKSYUBA3JQ5RknK6j8JpU4auwDb-u7q3k4dG9zLzp7ipPxmNqRhGLw2m_1DH7ZmrdkHoXXalecj6w3y6oRJ833UnLlWOpElXkY9d2hSHcaVqlX-a6-eqqJrZtYk74uwXqcajBEyLiWgNOr6Xg5m4hck8OVXzS0IhsfiNAuBcjxJqluStBGbseROMOgkiXbPv0jCPhHr-X3s96PXOf5CKrWtXCHe3eUjoov99yf7E5RaZ0q9xm57iz8zSDtErKw");
		request.addHeader("cache-control", "no-cache");
		
		HttpResponse response = client.execute(request);

		System.out.println("\nSending 'GET' request to URL : " + URL);
		System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		oraDocsJsonParser(result.toString());
		//JSONObject jsonRespose = new JSONObject(result.toString());
		
		
	}
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		OraDocsFolders ord = new OraDocsFolders();
		ord.getFolders();
	}
	
	private void oraDocsJsonParser(String data) {
			/*JSONParser parser = new JSONParser();
		    Object obj = parser.parse(new String(data));*/
		    Map<Object, Object> shareList = new HashMap<Object, Object>();
		    JSONObject jsonObject = new JSONObject(data);
		    JSONArray array = (JSONArray) jsonObject.get("items"); // it should be any array name

		    Iterator<Object> iterator = array.iterator();
		    while (iterator.hasNext())
		    {
		        Object it = iterator.next();
		        JSONObject folders = (JSONObject) it;
		        if(folders.get("type").equals("folder")) {
		        	shareList.put(folders.get("id"), folders.get("name"));
		        }
		        
		    }
		    Iterator it = shareList.entrySet().iterator();
		    while (it.hasNext())
		    {
		        Map.Entry value = (Map.Entry) it.next();
		        System.out.println("Folder id: " + value.getKey() + " and name: " + value.getValue());
		    }
	}

}

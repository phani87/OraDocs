package com.oracle.oradocs.folders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;
import org.json.JSONObject;

import jdk.nashorn.internal.parser.JSONParser;

public class OraDocsFolders {

	private final static String URL = "https://oradocs-corp.documents.us2.oraclecloud.com/documents/api/1.2/folders/";
	private final static String PUBLICLINK_URL = "https://oradocs-corp.documents.us2.oraclecloud.com/documents/api/1.2/publiclinks/";
	//private final static String MASTER_FOLDER = "F30CFB1F2E8D65077F6A5B0BF6C3FF17C1177A968060/";
	private static String MASTER_FOLDER = "";
	//private final static String MASTER_FOLDER = "FD1E7C6D74C8FAF7FFE52114F6C3FF17C1177A968060/"; // Shikhar
	private final String USER_AGENT = "Mozilla/5.0";
	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private List<FileVO> updatedFiles = new ArrayList<>();
	private static Log oraDoclogger = LogFactory.getLog(OraDocsFolders.class);
	
	private String token = "";
	
	
	private static FolderVO folderVo;
	
	public OraDocsFolders(String token, String MasterFolder) {
		this.token = token;
		this.MASTER_FOLDER = MasterFolder+"/";
	}
	
	

	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		new OraDocsFolders(
				"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6InUwY1BSU3dCYkE3V1BkZGR1MGo2QlBfRXZxVSIsImtpZCI6ImNvcnAuY2VydCJ9.eyJzdWIiOiJzaGlraGFyLm1pc2hyYUBvcmFjbGUuY29tIiwib3JhY2xlLm9hdXRoLnVzZXJfb3JpZ2luX2lkX3R5cGUiOiJMREFQX1VJRCIsIm9yYWNsZS5vYXV0aC51c2VyX29yaWdpbl9pZCI6InNoaWtoYXIubWlzaHJhQG9yYWNsZS5jb20iLCJpc3MiOiJjb3JwIiwib3JhY2xlLm9hdXRoLnN2Y19wX24iOiJjb3JwU2VydmljZVByb2ZpbGUiLCJpYXQiOjE1MDk2NDI2MjEsIm9yYWNsZS5vYXV0aC5wcm4uaWRfdHlwZSI6IkxEQVBfVUlEIiwib3JhY2xlLm9hdXRoLnRrX2NvbnRleHQiOiJyZXNvdXJjZV9hY2Nlc3NfdGsiLCJleHAiOjE1MTAyNDc0MjAsImF1ZCI6WyJodHRwczovL29yYWRvY3MtY29ycC5kb2N1bWVudHMudXMyLm9yYWNsZWNsb3VkLmNvbTo0NDMvZG9jdW1lbnRzIl0sIm9yYWNsZS5vYXV0aC5zdWIuaWRfdHlwZSI6IkxEQVBfVUlEIiwicHJuIjoic2hpa2hhci5taXNocmFAb3JhY2xlLmNvbSIsImp0aSI6IjM3NmU4MDc0LTJlZGQtNGRiZC05YjEzLWY1YTM4ZDViNWQ4MyIsIm9yYWNsZS5vYXV0aC5jbGllbnRfb3JpZ2luX2lkIjoiYmYyMDYxNTMtNWE0ZS00MjEyLTk5NTYtMjVhNmM0NDRlNDAyIiwib3JhY2xlLm9hdXRoLnNjb3BlIjoiaHR0cHM6Ly9vcmFkb2NzLWNvcnAuZG9jdW1lbnRzLnVzMi5vcmFjbGVjbG91ZC5jb206NDQzL2RvY3VtZW50cyIsInVzZXIudGVuYW50Lm5hbWUiOiJjb3JwIiwib3JhY2xlLm9hdXRoLmlkX2RfaWQiOiI2NjY4ODgxNzI5ODEyNTI1NCJ9.EG8W-vp3rrUEx0JhQ6FZWPhBGfqoAlRXiKCskiMLMCj6sPuNQWjo-evHZ9iqWITATuaMXHpdvms8rMbJnAw90z9UntTjxZhBl-tnnU1JYPpMHJ1Z4-z2T29OZV0cwVIQ7CSMswN9nItgC-OZ4zoRWFYnVVTFcNN7NTKnZWP147xmKhtd6iG2jVkdZyNxDdDARfDAzPnyaMeT2nmeirJrF8aucLqAFsrtdKsMFnihoIRINNmEKvX4yrcnlKEz8QQWbahMqnXHImSU24w0YJ2Z06egs3YxHsvDRdoj5Q08YBqFh8O9wJAduHCHkK4OnVymrnrJ146FQHzfnCl9_70cyg",
				"F18613ABE15F95E4881AEC01F6C3FF17C1177A968060").flowOrganizer();
		// new OraDocsFolders().get89date();
		oraDoclogger.info("Finished");
	}
	
	public List<FileVO> flowOrganizer() {
		List<FolderVO> MASTER_FOLDERS = null;
		String json_response = "";
		oraDoclogger.info("Entering FlowOrganizer Method");
		try {
			json_response = getFolders(URL + MASTER_FOLDER + "items");
			MASTER_FOLDERS = oraDocsJsonParser(json_response);
			iterateFolderList(MASTER_FOLDERS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return updatedFiles;

	}
	
	
	private void iterateFolderList(List<FolderVO> folderMap) {
		oraDoclogger.info("Iterating Folder");
		for(FolderVO folderID : folderMap) {
			try {
				String temp_json = getFolders(URL+folderID.getCurr_folder()+"/items");
				List<FolderVO> subFolderList = oraDocsJsonParser(temp_json);
				iterateFolderList(subFolderList);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//helper methods
	
	private List<FolderVO> oraDocsJsonParser(String data) {
		oraDoclogger.info("Parsing JSON response");
		List<FolderVO> folderMap = new ArrayList<FolderVO>();
		JSONObject jsonObject = new JSONObject(data);
		FolderVO folderVo = null;
		//System.out.println(jsonObject.toString());
		int size = Integer.parseInt(jsonObject.get("count").toString());
		if (size > 0) {
			JSONArray array = (JSONArray) jsonObject.get("items"); // it should be any array name
			List<FileVO> fileMap = new ArrayList<>();
			Iterator<Object> iterator = array.iterator();

			while (iterator.hasNext()) {
				Object it = iterator.next();
				JSONObject folders = (JSONObject) it;
				if (folders.get("type").equals("folder")) {
					folderVo = new FolderVO();
					folderVo.setCurr_folder(folders.get("id").toString());
					folderVo.setPrev_folder(folders.get("parentID").toString());
					folderVo.setFolder_name(folders.get("name").toString());
					folderVo.setTraversed(true);
					folderVo.setFilesUpdated(false);
					folderMap.add(folderVo);

				} else if (folders.get("type").equals("file")) {
					FileVO fileVo = new FileVO();
					fileVo.setFile_id(folders.get("id").toString());
					fileVo.setFolder_id(folders.get("parentID").toString());
					fileVo.setFile_name(folders.get("name").toString());
					JSONObject ownedBy = (JSONObject) folders.get("ownedBy");
					fileVo.setDisplay_name(ownedBy.get("displayName").toString());
					fileVo.setFileUpdated(true);
					fileMap.add(fileVo);
				}

			}
			if (fileMap.size() > 0) {
				updateFiles(fileMap);
			}
		}
		return folderMap;

	}
	
	private List<FileVO> updateFiles(List<FileVO> files) {
		for (FileVO file : files) {
			updatedFiles.add(file);
			String response_json = getPublicLink(file);
			iteratePublicLinks(response_json);
		}
		return updatedFiles;
	}
	
	private void iteratePublicLinks(String json_public_links) {
		List<String> publicLinkList = new ArrayList<String>();
		JSONObject publicLink = new JSONObject(json_public_links);
		/*System.out.println(publicLink.toString());*/
		int size = Integer.parseInt(publicLink.get("count").toString());
		if (size > 0) {
			JSONArray array = (JSONArray) publicLink.get("items");
			List<FileVO> fileMap = new ArrayList<>();
			Iterator<Object> iterator = array.iterator();
			while (iterator.hasNext()) {
				Object it = iterator.next();
				JSONObject pbLinks = (JSONObject) it;
				System.out.println("Updating Link Name:::"+pbLinks.get("linkName")+":::Updating Link:::"+pbLinks.get("linkID")+":::Expiration Time:::"+pbLinks.get("expirationTime"));
				publicLinkList.add(pbLinks.get("linkID").toString());
				updatePublicLink(publicLinkList);
			}
		}
	}
	
	private void updatePublicLink(List<String> publicLinkList) {
		
		String exp_89_days = get89date();
		
		String exp_json = "{\r\n" + 
				"    \"expirationTime\":\""+exp_89_days+"\",\r\n" + 
				"}";
		 
		
		/*String exp_json = "{\r\n" + 
				"    \"expirationTime\":\"2018-01-15T01:02:03\",\r\n" + 
				"}";*/
		
		if(publicLinkList.size()>0) {
			for(String pblcId : publicLinkList) {
				try {
				HttpClient client = new DefaultHttpClient();
				HttpPut request = new HttpPut(PUBLICLINK_URL+pblcId);
				StringBuffer result =null;
				request.addHeader("Content-Type", "application/json");
				request.addHeader("authorization", "Bearer "+token);
				request.addHeader("cache-control", "no-cache");
				request.setEntity(new StringEntity(exp_json) );
				
				HttpResponse response;
				try {
					response = client.execute(request);
					/*System.out.println("\nSending 'PUT' request to UPDATE LINK URL : " + PUBLICLINK_URL+pblcId);
					System.out.println("Response Code : " + response.getStatusLine().getStatusCode());*/

					BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

					result = new StringBuffer();
					String line = "";
					while ((line = rd.readLine()) != null) {
						result.append(line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	}
	
	private String get89date() {
		Date today_date = new Date();
		
		  //System.out.println(dateFormat.format(today_date));

	        // convert date to calendar
	        Calendar c = Calendar.getInstance();
	        c.setTime(today_date);

	        // manipulate date
	        c.add(Calendar.DATE, 88); //same with c.add(Calendar.DAY_OF_MONTH, 1);
	       

	        // convert calendar to date
	        Date currentDatePlusOne = c.getTime();
	        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
	       // System.out.println(dateFormat.format(currentDatePlusOne));
		return dateFormat.format(currentDatePlusOne);
		
	}
	
	private String getPublicLink(FileVO file) {


		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(PUBLICLINK_URL+"file/"+file.getFile_id());
		StringBuffer result =null;

		// add request header
		request.addHeader("authorization", "Bearer "+token);
		request.addHeader("cache-control", "no-cache");
		
		HttpResponse response;
		try {
			response = client.execute(request);
			//System.out.println("\nSending 'GET' request to PUBLIC LINK URL : " + PUBLICLINK_URL+file.getFile_id());
			//System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return  result.toString();
		
		
	
	}
	
	
	
	private String getFolders(String URL) throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL);

		// add request header
		request.addHeader("authorization", "Bearer "+token);
		request.addHeader("cache-control", "no-cache");
		
		HttpResponse response = client.execute(request);

		//System.out.println("\nSending 'GET' request to URL : " + URL);
		//System.out.println("Response Code : " + response.getStatusLine().getStatusCode());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		 return  result.toString();
		
		
	}

}

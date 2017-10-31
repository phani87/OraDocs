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

	private final static String URL = "https://oradocs-corp.documents.us2.oraclecloud.com/documents/api/1.2/folders/";
	private final static String MASTER_FOLDER = "F30CFB1F2E8D65077F6A5B0BF6C3FF17C1177A968060/";
	private final String USER_AGENT = "Mozilla/5.0";
	
	
	private static FolderVO folderVo;

	
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		new OraDocsFolders().flowOrganizer();
	}
	
	private void flowOrganizer() {
		List<FolderVO> MASTER_FOLDERS = null;
		String json_response = "";
		try {
			json_response = OraDocsFolders.getFolders(URL + MASTER_FOLDER + "items");
			MASTER_FOLDERS = new OraDocsFolders().oraDocsJsonParser(json_response);
			iterateFolderList(MASTER_FOLDERS);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
	private void iterateFolderList(List<FolderVO> folderMap) {
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
			List<FolderVO>  folderMap = new ArrayList<FolderVO>();
		    JSONObject jsonObject = new JSONObject(data);
		    FolderVO folderVo = null;
		    int size = Integer.parseInt(jsonObject.get("count").toString());
		    if(size>0) {
		    	JSONArray array = (JSONArray) jsonObject.get("items"); // it should be any array name
			    List<FileVO> fileMap = new ArrayList<>();
			    Iterator<Object> iterator = array.iterator();
			    
			    while (iterator.hasNext())
			    {
			    	//iterator.next();
			        Object it = iterator.next();
			        JSONObject folders = (JSONObject) it;
			        if(folders.get("type").equals("folder")) {
			        	folderVo = new FolderVO();
			        	folderVo.setCurr_folder(folders.get("id").toString());
			        	folderVo.setPrev_folder(folders.get("parentID").toString());
			        	folderVo.setFolder_name(folders.get("name").toString());
			        	folderVo.setTraversed(true);
			        	folderVo.setFilesUpdated(false);
			        	//System.out.println("Folder>>>" +folderVo.getPrev_folder()+"/"+folderVo.getCurr_folder());
			        	folderMap.add(folderVo);
			        	
			        }else if(folders.get("type").equals("file")) {
			        	FileVO fileVo = new FileVO();
			        	fileVo.setFile_id(folders.get("id").toString());
			        	fileVo.setFolder_id(folders.get("parentID").toString());
			        	fileVo.setFile_name(folders.get("name").toString());
			        	//fileVo.setPrev_folder_id(folders.get("parentID").toString());
			        	fileVo.setFileUpdated(true);
			        	fileMap.add(fileVo);
			        }
			        
			    }
			    if(fileMap.size()>0) {
			    	updateFiles(fileMap);
			    }
		    }
		    return folderMap;
		    
	}
	
	private void updateFiles(List<FileVO> files) {
		for(FileVO file : files) {
			System.out.println("Updating..."+file.getFile_name());
			FolderVO folderVo = new FolderVO();
			//folderVo.setCurr_folder(curr_folder);
		}
	}
	private static String getFolders(String URL) throws ClientProtocolException, IOException {

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(URL);

		// add request header
		request.addHeader("authorization", "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsIng1dCI6InUwY1BSU3dCYkE3V1BkZGR1MGo2QlBfRXZxVSIsImtpZCI6ImNvcnAuY2VydCJ9.eyJzdWIiOiJzaGlraGFyLm1pc2hyYUBvcmFjbGUuY29tIiwib3JhY2xlLm9hdXRoLnVzZXJfb3JpZ2luX2lkX3R5cGUiOiJMREFQX1VJRCIsIm9yYWNsZS5vYXV0aC51c2VyX29yaWdpbl9pZCI6InNoaWtoYXIubWlzaHJhQG9yYWNsZS5jb20iLCJpc3MiOiJjb3JwIiwib3JhY2xlLm9hdXRoLnN2Y19wX24iOiJjb3JwU2VydmljZVByb2ZpbGUiLCJpYXQiOjE1MDkzOTU0MzAsIm9yYWNsZS5vYXV0aC5wcm4uaWRfdHlwZSI6IkxEQVBfVUlEIiwib3JhY2xlLm9hdXRoLnRrX2NvbnRleHQiOiJyZXNvdXJjZV9hY2Nlc3NfdGsiLCJleHAiOjE1MTAwMDAyMjksImF1ZCI6WyJodHRwczovL29yYWRvY3MtY29ycC5kb2N1bWVudHMudXMyLm9yYWNsZWNsb3VkLmNvbTo0NDMvZG9jdW1lbnRzIl0sIm9yYWNsZS5vYXV0aC5zdWIuaWRfdHlwZSI6IkxEQVBfVUlEIiwicHJuIjoic2hpa2hhci5taXNocmFAb3JhY2xlLmNvbSIsImp0aSI6ImQ4NGRmNTA1LWM1ZWYtNDA5Yy05YTcxLTc0MGRlMzk3ODlmOSIsIm9yYWNsZS5vYXV0aC5jbGllbnRfb3JpZ2luX2lkIjoiYmYyMDYxNTMtNWE0ZS00MjEyLTk5NTYtMjVhNmM0NDRlNDAyIiwib3JhY2xlLm9hdXRoLnNjb3BlIjoiaHR0cHM6Ly9vcmFkb2NzLWNvcnAuZG9jdW1lbnRzLnVzMi5vcmFjbGVjbG91ZC5jb206NDQzL2RvY3VtZW50cyIsInVzZXIudGVuYW50Lm5hbWUiOiJjb3JwIiwib3JhY2xlLm9hdXRoLmlkX2RfaWQiOiI2NjY4ODgxNzI5ODEyNTI1NCJ9.oUNxqzNsdnx2DtHwehF62_6mcNQE4JYRanS-RHfAR0Ao-gNwS93DpXDDbn15KqvM91CTv_Hi6k-hFf7S9k7KjiW54rxVcFrJwG2aLsxA0qZ7_tG2DSu7jhPYXYg7Ke8qrk1mlwzTmXgGDHjXDloqXJH_GwNiOm2VbrCODb40VsC_rfJNAyMJ0_7lXem95af2kSER3ijJ5ylnwein94SSMtPQbtlzPiqbwUgLPKm9xMqQUYb7vWqOyeVz9c5krRI8srbWHh7_vQtwFJuho8KbM0_fVIcy3mFIu5ohF08Vr97vze8oCJZxj5BhI3HrA0Y1HoeAtbZ-CKTFakMTERX-0Q");
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
		 return  result.toString();
		
		
	}

}

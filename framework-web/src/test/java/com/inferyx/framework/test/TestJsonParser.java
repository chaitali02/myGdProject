package com.inferyx.framework.test;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TestJsonParser {
	
	public static void main(String []args) throws JSONException {
		String jsonString = "{    \"dependsOn\" : {   \"type\" : \"dag\",   \"uuid\" : \"5fa7c436-81d6-4ce8-a778-ee6dd2d57f06\",   \"version\" : \"1488620444\"    },    \"execParams\" : {   \"refKeyList\" : [   { \"type\" : \"datapod\", \"uuid\" : \"0c9b5122-ced3-404d-a22b-ff1105e1f9ac\", \"version\" : \"1488623530\"  },   { \"type\" : \"function\", \"uuid\" : \"9e3de26e-02fb-11e7-93ae-92361f002671\", \"version\" : \"1488801521\"  },   { \"type\" : \"map\", \"uuid\" : \"4f875b10-9534-4f9d-b9e5-b4cae8e7bf0d\", \"version\" : \"1490412970\"  },   { \"type\" : \"rule\", \"uuid\" : \"c174177a-249b-4175-9c73-a26f3b12e489\", \"version\" : \"1490410106\"  }   ]    },    \"xPos\" : 140.0,    \"yPos\" : 90.0,    \"stages\" : [    {  \"stageId\" : \"0\",  \"dependsOn\" : [],  \"tasks\" : [  {     \"taskId\" : \"0\",     \"dependsOn\" : [],     \"name\" : \"map_dim_transaction_type\",     \"operators\" : [     {   \"dependsOn\" : [],   \"operatorInfo\" : {  \"ref\" : { \"type\" : \"map\", \"uuid\" : \"4f875b10-9534-4f9d-b9e5-b4cae8e7bf0d\", \"version\" : \"1490412970\"  }   },   \"operatorParams\" : {}    }     ],     \"xPos\" : 860.0,     \"yPos\" : 90.0,     \"status\" : [     {   \"stage\" : \"NotStarted\"   }     ] }  ],  \"name\" : \"Populate dim_transaction_type\",  \"xPos\" : 510.0,  \"yPos\" : 90.0,  \"status\" : [  {     \"stage\" : \"NotStarted\" }  ]   }    ],    \"status\" : [    {  \"stage\" : \"NotStarted\"   }    ],    \"uuid\" : \"f9e7e515-e8f9-451d-b019-6b61af56272a\",    \"version\" : \"1497569923\",    \"name\" : \"sys_f9e7e515-e8f9-451d-b019-6b61af56272a\",    \"createdBy\" : {   \"ref\" : {  \"type\" : \"user\",  \"uuid\" : \"d04716df-e96a-419f-9118-c81342b47f86\"   }    },    \"active\" : \"Y\",    \"appInfo\" : [    {  \"ref\" : { \"type\" : \"application\", \"uuid\" : \"d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd\"  }   }    ]}";
		JSONObject jsonObject = new JSONObject(jsonString);
		System.out.println(parseJsonObject(jsonObject));
    	
	}
	
	public static String parseJsonObject(JSONObject jsonObject) throws JSONException {
		Iterator<String> iter = jsonObject.keys();
		JSONArray jsonArray = null;
		JSONObject nestedObj = null;
		String value = null;
		boolean findRefs = false;
    	while (iter.hasNext()) {
    		String key = iter.next();
    		System.out.println(key);
    		jsonArray = jsonObject.optJSONArray(key);
    		nestedObj = jsonObject.optJSONObject(key);
    		value = jsonObject.optString(key);
    		if (jsonArray != null) {
    			System.out.println(" Array in : " + key);
    			if (key.startsWith("ref")) {
    				findRefs = Boolean.TRUE;
    			}
    			parseJsonArray(jsonArray, findRefs);
    			findRefs = Boolean.FALSE;
    		} else if (nestedObj != null) {
    			System.out.println(" Nested object in : " + key);
    			if (key.equals("ref")) {
    				String uuid = nestedObj.getString("uuid");
    				String version = nestedObj.optString("version");
    				String type = nestedObj.getString("type");
    				if ((nestedObj.has("attrId") || nestedObj.has("attributeId")) && nestedObj.getString("type").equals("datapod")) {
    					continue;
    				} else {
    					System.out.println("Create edge for : " + type + " : " + uuid + " : " + version);
    				}
    			} else {
    				parseJsonObject(nestedObj);
    			}
    		} else if (value != null) {
    			// System.out.println(" String in : " + key);
    			continue;
    		}
    	}
    	return null;
	}
	
	public static String parseJsonArray(JSONArray jsonArray, boolean findRefs) throws JSONException {
		JSONArray nestedArray = null;
		JSONObject nestedObj = null;
		String value = null;
		if (jsonArray == null) {
			return null;
		}
		for( int i = 0; i < jsonArray.length(); i++) {
			nestedArray = jsonArray.optJSONArray(i);
    		nestedObj = jsonArray.optJSONObject(i);
    		value = jsonArray.optString(i);
    		if (nestedArray != null) {
    			System.out.println(" Array in : " + i);
    			parseJsonArray(nestedArray, Boolean.FALSE);
    		} else if (nestedObj != null) {
    			System.out.println(" Nested object in : " + i);
    			if (findRefs) {
    				String uuid = nestedObj.getString("uuid");
    				String version = nestedObj.optString("version");
    				String type = nestedObj.getString("type");
    				System.out.println("Create edge for : " + type + " : " + uuid + " : " + version);
    			}
    			parseJsonObject(nestedObj);
    		} else if (value != null) {
    			// System.out.println(" String in : " + key);
    			continue;
    		}
		}
		return null;
	}

}

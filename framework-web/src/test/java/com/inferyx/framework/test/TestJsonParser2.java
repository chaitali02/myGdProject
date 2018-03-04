package com.inferyx.framework.test;

import java.util.Iterator;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.inferyx.framework.common.Helper;
import com.inferyx.framework.domain.MetaIdentifier;
import com.inferyx.framework.service.MetadataServiceImpl;

public class TestJsonParser2 {
	
	private static MetadataServiceImpl metadataServiceImpl = new MetadataServiceImpl();
		
	public static void main(String []args) throws JSONException {
		String jsonString = "{ \"dependsOn\" : { \"type\" : \"dag\", \"uuid\" : \"6dd713ed-3ea9-4940-8c36-8b872c7576e2\", \"version\" : \"1460270907\" }, \"execParams\" : { \"refKeyList\" : [ { \"type\" : \"function\", \"uuid\" : \"9e3de372-02fb-11e7-93ae-92361f002671\", \"version\" : \"1488801521\" }, { \"type\" : \"function\", \"uuid\" : \"9e3de26e-02fb-11e7-93ae-92361f002671\", \"version\" : \"1488801521\" }, { \"type\" : \"datapod\", \"uuid\" : \"ea12caea-0eb1-4b48-ad78-c8d17db3233e\", \"version\" : \"1488623611\" }, { \"type\" : \"datapod\", \"uuid\" : \"80794b71-8707-4f71-ae20-f22e839a8279\", \"version\" : \"1488623675\" }, { \"type\" : \"rule\", \"uuid\" : \"12a2a192-1884-495b-9c80-d3543dbe4a56\", \"version\" : \"1490409822\" }, { \"type\" : \"map\", \"uuid\" : \"ec398401-8495-419f-bde7-76db137f3088\", \"version\" : \"1490413075\" }, { \"type\" : \"rule\", \"uuid\" : \"c81d3517-4501-49e7-a385-d948526f5d30\", \"version\" : \"1491486176\" }, { \"type\" : \"map\", \"uuid\" : \"6356a9f8-f5c5-454b-9cbe-0d6ecc452b1a\", \"version\" : \"1490413230\" }, { \"type\" : \"datapod\", \"uuid\" : \"57f76265-e1f5-41b8-afa4-cd06197bf849\", \"version\" : \"1488623492\" }, { \"type\" : \"map\", \"uuid\" : \"850eec61-2047-4d94-aab1-6117d7f84bd6\", \"version\" : \"1490412923\" }, { \"type\" : \"rule\", \"uuid\" : \"424decc0-f29d-4dbd-ae74-476335ca6f8f\", \"version\" : \"1491485346\" }, { \"type\" : \"map\", \"uuid\" : \"ada38baf-1be2-4579-94b4-4b7722c50622\", \"version\" : \"1491485523\" }, { \"type\" : \"map\", \"uuid\" : \"bfef7565-e873-42ba-9e60-482751a24003\", \"version\" : \"1490413020\" }, { \"type\" : \"map\", \"uuid\" : \"a89f5339-0c89-43a2-8898-b279764f6a19\", \"version\" : \"1491486316\" }, { \"type\" : \"map\", \"uuid\" : \"d2cc6181-9104-4f2d-b220-2cd29a69dabe\", \"version\" : \"1490413174\" }, { \"type\" : \"datapod\", \"uuid\" : \"3cdb9b07-d449-4dfc-af2f-3b1245eb30e5\", \"version\" : \"1489649539\" }, { \"type\" : \"rule\", \"uuid\" : \"b7ea1bde-72fe-44b1-8a2b-2341e9158e62\", \"version\" : \"1490410179\" }, { \"type\" : \"map\", \"uuid\" : \"4f875b10-9534-4f9d-b9e5-b4cae8e7bf0d\", \"version\" : \"1490412970\" }, { \"type\" : \"rule\", \"uuid\" : \"76d1032e-c5ba-4ade-bb42-f782f9e14a5e\", \"version\" : \"1490410443\" }, { \"type\" : \"map\", \"uuid\" : \"f06e9f5d-3b8b-4daf-9900-a9b641cbb5db\", \"version\" : \"1490413126\" }, { \"type\" : \"datapod\", \"uuid\" : \"44e7dd6f-e8e6-41fa-a2fd-9b2e9b25ba98\", \"version\" : \"1488623458\" }, { \"type\" : \"rule\", \"uuid\" : \"c174177a-249b-4175-9c73-a26f3b12e489\", \"version\" : \"1490410106\" }, { \"type\" : \"datapod\", \"uuid\" : \"e5a12713-0b1d-4d2a-b082-a65cad576e94\", \"version\" : \"1489650533\" }, { \"type\" : \"datapod\", \"uuid\" : \"0c9b5122-ced3-404d-a22b-ff1105e1f9ac\", \"version\" : \"1488623530\" }, { \"type\" : \"datapod\", \"uuid\" : \"52bbfced-1b47-41fa-94a8-2e9f7bd9f725\", \"version\" : \"1488623831\" }, { \"type\" : \"rule\", \"uuid\" : \"ff2e7c87-b92b-4dfa-afaa-bd50b703f5cc\", \"version\" : \"1490410384\" }, { \"type\" : \"rule\", \"uuid\" : \"8f151e7e-dd77-4f51-8d0d-aa8ed11c0006\", \"version\" : \"1490410330\" }, { \"type\" : \"datapod\", \"uuid\" : \"14bebb86-6137-4109-ad45-0eea53066768\", \"version\" : \"1488623718\" }, { \"type\" : \"rule\", \"uuid\" : \"42c2a260-5f2d-47c2-a184-2ace0ae82312\", \"version\" : \"1490410260\" } ] }, \"xPos\" : 70.0, \"yPos\" : 370.0, \"stages\" : [ { \"stageId\" : \"0\", \"dependsOn\" : [], \"tasks\" : [ { \"taskId\" : \"0\", \"dependsOn\" : [], \"name\" : \"Load dim_bank\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"7e3b8cc1-4eca-43fb-887a-d022588ff1eb\", \"version\" : \"1497705643\" } }, \"operatorParams\" : {} } ], \"xPos\" : 690.0, \"yPos\" : 230.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:44.384Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.779Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:20:59.052Z\" } ] }, { \"taskId\" : \"1\", \"dependsOn\" : [], \"name\" : \"Load dim_branch\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"d657c1da-9303-439a-aab4-05287f1596a0\", \"version\" : \"1497705644\" } }, \"operatorParams\" : {} } ], \"xPos\" : 690.0, \"yPos\" : 310.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:45.569Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.784Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:20:59.528Z\" } ] }, { \"taskId\" : \"2\", \"dependsOn\" : [], \"name\" : \"Load dim_address\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"67718ac9-ec1b-48c5-9fff-148ce9e329b6\", \"version\" : \"1497705645\" } }, \"operatorParams\" : {} } ], \"xPos\" : 690.0, \"yPos\" : 400.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:46.299Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.790Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:20:59.106Z\" } ] }, { \"taskId\" : \"3\", \"dependsOn\" : [], \"name\" : \"Load dim_account\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"4457251a-6257-4af4-8f3b-19813e75c8eb\", \"version\" : \"1497705646\" } }, \"operatorParams\" : {} } ], \"xPos\" : 690.0, \"yPos\" : 150.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:47.492Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.798Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:21:00.533Z\" } ] }, { \"taskId\" : \"4\", \"dependsOn\" : [], \"name\" : \"Load dim_customer\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"4f4cc6d7-8b46-4d3c-8f6b-764a44b4b6d4\", \"version\" : \"1497705647\" } }, \"operatorParams\" : {} } ], \"xPos\" : 690.0, \"yPos\" : 560.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:48.508Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.802Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:21:00.492Z\" } ] }, { \"taskId\" : \"5\", \"dependsOn\" : [], \"name\" : \"Load dim_transaction_type\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"3516140a-d908-4d15-8f37-e53bc0e80f71\", \"version\" : \"1497705648\" } }, \"operatorParams\" : {} } ], \"xPos\" : 690.0, \"yPos\" : 480.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:49.235Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.821Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:20:59.500Z\" } ] }, { \"taskId\" : \"6\", \"dependsOn\" : [ \"0\", \"1\", \"2\", \"3\", \"4\", \"5\" ], \"name\" : \"Load fact_transaction\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"cbf83a6d-1807-4224-99f7-bf08a3692c8b\", \"version\" : \"1497705649\" } }, \"operatorParams\" : {} } ], \"xPos\" : 1040.0, \"yPos\" : 370.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:50.850Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:21:02.835Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:21:07.496Z\" } ] }, { \"taskId\" : \"7\", \"dependsOn\" : [ \"6\" ], \"name\" : \"Load fact_account_summary_monthly\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"4bc6410e-9c59-4655-b5bf-ebb5fd1d781b\", \"version\" : \"1497705650\" } }, \"operatorParams\" : {} } ], \"xPos\" : 1210.0, \"yPos\" : 320.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:51.488Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:21:12.847Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:21:16.526Z\" } ] }, { \"taskId\" : \"8\", \"dependsOn\" : [ \"6\" ], \"name\" : \"Load fact_customer_summary_monthly\", \"operators\" : [ { \"dependsOn\" : [], \"operatorInfo\" : { \"ref\" : { \"type\" : \"mapExec\", \"uuid\" : \"0360dbee-31a0-4045-af5e-97f31c69acef\", \"version\" : \"1497705651\" } }, \"operatorParams\" : {} } ], \"xPos\" : 1210.0, \"yPos\" : 450.0, \"status\" : [ { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:52.066Z\" }, { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:21:12.854Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:21:16.572Z\" } ] } ], \"name\" : \"Populate dimensions,fact and aggregated facts\", \"xPos\" : 330.0, \"yPos\" : 370.0, \"status\" : [ { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.779Z\" }, { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:42.414Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:21:16.572Z\" } ] } ], \"status\" : [ { \"stage\" : \"InProgress\", \"createdOn\" : \"2017-06-17T13:20:52.779Z\" }, { \"stage\" : \"NotStarted\", \"createdOn\" : \"2017-06-17T13:20:42.565Z\" }, { \"stage\" : \"Completed\", \"createdOn\" : \"2017-06-17T13:21:16.572Z\" } ], \"uuid\" : \"f9d3e234-0293-4242-9b10-b59bdf04790e\", \"version\" : \"1497705642\", \"name\" : \"sys_f9d3e234-0293-4242-9b10-b59bdf04790e\", \"createdBy\" : { \"ref\" : { \"type\" : \"user\", \"uuid\" : \"d04716df-e96a-419f-9118-c81342b47f86\" } }, \"createdOn\" : \"2017-06-17T13:20:42.000Z\", \"active\" : \"Y\", \"appInfo\" : [ { \"ref\" : { \"type\" : \"application\", \"uuid\" : \"d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd\" } } ] }";
		JSONObject jsonObject = new JSONObject(jsonString);
		createVnE(jsonObject);
    	
	}
	
	public static void createVnE(JSONObject jsonObject) throws JSONException {

		Iterator<String> iter = jsonObject.keys();
		JSONArray jsonArray = null;
		String value = null;

		while (iter.hasNext()) {
    		String key = iter.next();
    		System.out.println("Processing key:" + key);
    		jsonArray = jsonObject.optJSONArray(key);
    		JSONObject childObj = jsonObject.optJSONObject(key);
    		value = jsonObject.optString(key);
    		
    		if (childObj != null && value.startsWith("{",0)) {
    			System.out.println("Key is an Object");
    			createVnE(childObj);
    		}
    		if (jsonArray != null) {
    			System.out.println("Key is an Array");
    			for( int i = 0; i < jsonArray.length(); i++) {
    				  value = jsonArray.optString(i);
    				  childObj = jsonArray.optJSONObject(i);
    				  if (childObj != null)
    					  createVnE(childObj);
    			}
    		}
    		else if (key.startsWith("type")) {
    			System.out.println("Key is a MetaIdentifier");
    			MetaIdentifier mi = new MetaIdentifier();
    			mi.setType(Helper.getMetaType(jsonObject.optString(key)));
    			while (iter.hasNext()) {
    				String edgeKey = iter.next();
    				if (edgeKey.startsWith("uuid"))
    					mi.setUuid(jsonObject.optString(edgeKey));
    				if (edgeKey.startsWith("version"))
    					mi.setVersion(jsonObject.optString(edgeKey));
    			}
    			System.out.println("Creating edge..." + mi);
    		}
    	}
	}
	
}

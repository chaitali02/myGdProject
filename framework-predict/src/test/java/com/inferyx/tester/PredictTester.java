/**
 * 
 */
package com.inferyx.tester;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inferyx.framework.domain.Application;
import com.inferyx.framework.domain.DeployExec;
import com.inferyx.framework.domain.MetaIdentifierHolder;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.domain.Status;

/**
 * @author joy
 *
 */
public class PredictTester implements Runnable {

	String port = "8089";
	String appId = "d7c11fd7-ec1a-40c7-ba25-7da1e8b730cz";
	String userId = "d04716df-e96a-419f-9118-c81342b47f86";
	String state = null;

	/**
	 * 
	 */
	public PredictTester() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) throws IOException {
		PredictTester tester = new PredictTester();
		Thread thread = new Thread(tester);
		thread.start();
	}

	public void startService() throws IOException {
		String path = "/app/framework_predict";
		String processName = "predictStarter";
		String processPath = path + "/bin/" + processName;
		// String port = "8089";

		ProcessBuilder pb = new ProcessBuilder(processPath, this.port, "2>&1", "&");
		System.out.println("Declaring process");
		try {
			new Thread(new Runnable() {

				@Override
				public void run() {
					BufferedOutputStream out = new BufferedOutputStream(System.out);
					BufferedOutputStream err = new BufferedOutputStream(System.err);
					Process process = null;
					try {
						process = pb.start();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					System.out.println("Starting process");
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					BufferedReader brErr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					String line = null;
					String errLine = null;
					try {
						while ((errLine = brErr.readLine()) != null || (line = br.readLine()) != null) {
							System.out.print((StringUtils.isNotBlank(line) ? line + "\n" : "")
									+ (StringUtils.isNotBlank(errLine) ? errLine + "\n" : ""));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}).start();
			System.out.println(" Starting service ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String pollingForStart() {
		String state = null;
		String url = "http://localhost:" + this.port + "/starter/monitor/getProcessStatus";
		RestTemplate restTemplate = new RestTemplate();
		System.out.println(" Before call ");
		long startTime = System.currentTimeMillis();
		state = restTemplate.getForObject(url, String.class);
		// restTemplate.delete(url);
		System.out.println(" State : " + state + " : In time : " + (System.currentTimeMillis() - startTime) / 1000);
		return state;
	}
	
	public boolean deployModel(String trainExecUuid, String trainExecVersion, String processStatus) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String state = null;
		boolean deployStatus = getDeployStatus(trainExecUuid, trainExecVersion);
		boolean response = false;
		
		if(!deployStatus && processStatus.equalsIgnoreCase("ALIVE")) {
//			deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.InProgress);
			//deploy code
//			Application application = commonServiceImpl.getApp();
//			MetaIdentifierHolder userInfo = securityServiceImpl.getuserInfo();
			String deployURL = "http://localhost:"+this.port
								+ "/starter/model/deploy?"
								+ "trainExec_uuid="+trainExecUuid
								+ "&version="+trainExecVersion
								+ "&userId="+this.userId
								+ "&appId="+this.appId;
			
			RestTemplate restTemplate = new RestTemplate();
			HashMap<String, String> parameters = new HashMap<>();
			parameters.put("trainExec_uuid", trainExecUuid);
			parameters.put("version", trainExecVersion);
			parameters.put("userId", this.userId);
			parameters.put("appId", this.appId);
			response = restTemplate.postForObject(deployURL, null, boolean.class, parameters);
			/*if(response) {
				deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.Completed);
				
			} else {
				deployExec = (DeployExec) commonServiceImpl.setMetaStatus(deployExec, MetaType.deployExec, Status.Stage.Failed);
			}*/
			System.out.println("Model deployed response: "+response);
		}
		return response;
	}
	
	public boolean getDeployStatus(String trainExecUuid, String trainExecVersion) throws JsonProcessingException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
		String deployURL = "http://localhost:"+this.port
							+ "/starter/model/getDeployStatus?"
							+ "trainExec_uuid="+trainExecUuid
							+ "&version="+trainExecVersion
							+ "&userId="+this.userId
							+ "&appId="+this.appId;
		
		RestTemplate restTemplate = new RestTemplate();
		boolean response = restTemplate.getForObject(deployURL, boolean.class);
		System.out.println("Get deploy status response: "+response);
		
		return response;
	}

	@Override
	public void run() {
		String trainExecUuid = "0b30ced3-2a66-4ecb-a075-fc08668718a1";
		String trainExecVersion = "1545538602";
		try {
			startService();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(" Going to poll for started status ");
		String state = null;
		while (StringUtils.isBlank(state) || !state.equalsIgnoreCase("ALIVE")) {
			try {
				Thread.sleep(5000);
				System.out.println(" Ready to poll ");
				state = pollingForStart();
				System.out.println(" State within try block");
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("STATE : " + state);
		}
		try {
			deployModel(trainExecUuid, trainExecVersion, state);
			getDeployStatus(trainExecUuid, trainExecVersion);
		} catch (JsonProcessingException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | NullPointerException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

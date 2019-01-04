/**
 * 
 */
package com.inferyx.predict.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.inferyx.predict.domain.MonitorDomain;

/**
 * @author joy
 *
 */
@RestController
@RequestMapping(value = "/starter/monitor")
public class MonitorController {

	/**
	 * 
	 */
	public MonitorController() {
		// TODO Auto-generated constructor stub
	}
	
	@RequestMapping(value = "/getProcessStatus", method = RequestMethod.GET)
	public String ping() {
		return "ALIVE";
	}
	
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public MonitorDomain periodicCheck() {
		int mb = 1024*1024;

		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		System.out.println("##### Heap utilization statistics [MB] #####");

		float usedMem = (runtime.totalMemory() - runtime.freeMemory()) / mb;
		float freeMem = runtime.freeMemory() / mb;
		float totMem = runtime.totalMemory() / mb;
		float maxMem = runtime.maxMemory() / mb;
		int avblProcessors = runtime.availableProcessors();
		//Print used memory
		System.out.println("Used Memory:"
			+ usedMem);

		//Print free memory
		System.out.println("Free Memory:"
			+ freeMem);

		//Print total available memory
		System.out.println("Total Memory:" + totMem);

		//Print Maximum available memory
		System.out.println("Max Memory:" + maxMem);
		
		// Print available processors
		System.out.println("Available Processors : " + avblProcessors);
		
		MonitorDomain monDomain = new MonitorDomain();
		monDomain.setAvailableProcessors(avblProcessors);
		monDomain.setUsedMem(usedMem);
		monDomain.setFreeMem(freeMem);
		monDomain.setTotMem(totMem);
		monDomain.setMaxMem(maxMem);
		return monDomain;
	}
}

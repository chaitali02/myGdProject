/**
 * 
 */
package com.inferyx.framework.demo;

import org.springframework.stereotype.Component;

/**
 * @author joy
 *
 */
@Component
public class ScheduledProcessor {

   public void process() {
      System.out.println("Scheduler triggered. Submitting batch...");
   }

}
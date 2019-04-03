/**
 * 
 */
package com.inferyx.framework.demo;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author joy
 *
 */
@Component
public class ScheduleChanger {

   @Autowired
   private DynamicSchedule dynamicSchedule;

//   @Scheduled(fixedDelay=30000)
  
   /**************************Unused***********************/
   /* public void change() {
	  System.out.println("Inside ScheduleChanger");
      Random rnd = new Random();
      int nextTimeout = rnd.nextInt(30000);
      System.out.println("Changing poll time to: " + nextTimeout);
      //dynamicSchedule.reset(nextTimeout);
   }*/

}

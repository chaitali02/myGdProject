/**
 * 
 */
package com.inferyx.framework.demo;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.inferyx.framework.domain.Batch;
import com.inferyx.framework.domain.MetaType;
import com.inferyx.framework.service.BatchServiceImpl;
import com.inferyx.framework.service.CommonServiceImpl;

/**
 * @author joy
 *
 */
@Component
public class ScheduleChanger {

   @Autowired
   private DynamicSchedule dynamicSchedule;
   @Autowired
   private CommonServiceImpl<?> commonServiceImpl;
   @Autowired
   private BatchServiceImpl batchServiceImpl;

   @Scheduled(fixedDelay=30000)
   public void change() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NullPointerException, ParseException {
	  System.out.println("Inside ScheduleChanger");
      Random rnd = new Random();
      int nextTimeout = rnd.nextInt(30000);
      System.out.println("Changing poll time to: " + nextTimeout);
//      List<Batch> scheduledTasks = batchServiceImpl.getLatestBatch((List<Batch>) commonServiceImpl.findAllLatestWithoutAppUuid(MetaType.batch));
      dynamicSchedule.reset(nextTimeout);
   }

}

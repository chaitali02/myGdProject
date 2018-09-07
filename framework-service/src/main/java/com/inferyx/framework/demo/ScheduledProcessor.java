/**
 * 
 */
package com.inferyx.framework.demo;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * @author joy
 *
 */
@Component
public class ScheduledProcessor {

   private final AtomicInteger counter = new AtomicInteger();

   public void process() {
      System.out.println("Scheduler started " + new Date());
//		Map<Date, List<MetaIdentifierHolder>> scheduleMap = getLatestBatch();      
//		List<Batch> batchs = new ArrayList<>();
//		 for(MetaIdentifierHolder batchHolder : new ArrayList<>(scheduleMap.values()).get(0)) {
//			 MetaIdentifier batchMI = batchHolder.getRef();
//			 Batch batch = (Batch) commonServiceImpl.getOneByUuidAndVersion(batchMI.getUuid(), batchMI.getVersion(), batchMI.getType().toString());
//			 batchs.add(batch);
   }

}
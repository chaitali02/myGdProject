/**
 * 
 */
package com.inferyx.framework.demo;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * @author joy
 *
 */
@Component
public class ScheduledProcessor {

   private final AtomicInteger counter = new AtomicInteger();

   /******************Unused***********************/
   /*public void process() {
      System.out.println("processing next 10 at " + new Date());
      for (int i = 0; i < 10; i++) {
         System.out.println("Counter : " + counter.incrementAndGet());
      }
   }*/

}
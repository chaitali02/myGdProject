package com.inferyx.framework.test;

import static org.junit.Assert.*;
import org.junit.Test;

public class SSHManagerTest {
/**
 * Test of sendCommand method, of class SSHManager.
 */
@Test
public void testSendCommand()
{
    System.out.println("sendCommand");

    String command = "python3 /home/rohini/Desktop/train_linear_regression_model_tensorflow.py";
   
//    String command = "python3\n"
//    				+"import subprocess\n" 
//    				+"subprocess.call(['python3', '192.168.0.104:22/home/rohini/Desktop/1c0c6bdc-bc9f-4755-a4a2-47ccde5b6193_1540965064.py'])";
//    
//    command =  "python3 -c "+"\"import pathos " + 
//    	    "c = pathos.core.copy('1c0c6bdc-bc9f-4755-a4a2-47ccde5b6193_1540965064.py', destination='192.168.0.104:/home/gridedge-5/Desktop/1c0c6bdc-bc9f-4755-a4a2-47ccde5b6193_1540965064.py') " + 
//    	    "s = pathos.core.execute('1c0c6bdc-bc9f-4755-a4a2-47ccde5b6193_1540965064.py', host='192.168.0.104')\"";
    
    String userName = "vaibhav";
    String password = "gridedge";
    String connectionIP = "10.10.0.10";
    SSHManager instance = new SSHManager(userName, password, connectionIP, "");
    String errorMessage = instance.connect();

    if(errorMessage != null)
    {
        System.out.println(errorMessage);
        fail();
    }

    String expResult = "thisOne\n";
    // call sendCommand for each command and the output 
    //(without prompts) is returned
    String result = instance.sendCommand(command);
    System.out.println(result + " (executed sussessfully)");
    // close only after all commands are sent
    instance.close();
    assertEquals(expResult, result);
}
}
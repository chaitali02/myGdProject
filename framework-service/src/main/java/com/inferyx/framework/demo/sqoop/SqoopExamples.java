/**
 * 
 */
package com.inferyx.framework.demo.sqoop;

import com.cloudera.sqoop.SqoopOptions;
import com.cloudera.sqoop.SqoopOptions.IncrementalMode;
import com.cloudera.sqoop.tool.ImportTool;

/**
 * @author joy
 *
 */

 
public class SqoopExamples {
 
  private static SqoopOptions sqoopOptions = new SqoopOptions();
  private static final String connectionString = "jdbc:mysql://127.0.0.1:3306/mydatabase";
  private static final String username = "root";
  private static final String password = "root";
  
  public static void main(String[] args) {
	  
	  setUp();
	 
	  tansferEntireTableSpecificDir("customer","/user/cloudera/ingest/raw/customers");
	 
	  runIt();
	}
 
  private static void setUp() {
    sqoopOptions.setConnectString(connectionString);
    sqoopOptions.setUsername(username);
    sqoopOptions.setPassword(password);
  }
 
  private static int runIt() {
    int res;
    res = new ImportTool().run(sqoopOptions);
    if (res != 0) {
      throw new RuntimeException("Sqoop API FAILED - return code : "+Integer.toString(res));
    }
    return res;
  }
 
  private static void transferEntireTable(String table) {
    sqoopOptions.setTableName(table);
  }
 
  private static void tansferEntireTableSpecificDir(String table,
  String directory) {
    transferEntireTable(table);
    sqoopOptions.setWarehouseDir(directory);
  }
 
  private static void tansferringEntireTableSpecificDirHiveMerge(String table,
  String directory) {
    tansferEntireTableSpecificDir(table,directory);
    sqoopOptions.setHiveImport(true);
  }
 
 /****************************Unused***********************/
  /* private static void tansferringEntireTableSpecificDirHivePartitionMerge(String table
    , String directory
    , String partitionKey
    , String partitionValue) {
    tansferringEntireTableSpecificDirHiveMerge(table,directory);
    sqoopOptions.setHivePartitionKey(partitionKey);
    sqoopOptions.setHivePartitionValue(partitionValue);
  }*/
 
  
  /****************************Unused**********************/
  /*private static void tansferringEntireTableWhereClause(String table,
    String whereClause) {
    //To do
  }*/
 
  /*********************Unused***************************/
  /*private static void compressImportedData(String table, String directory
  ,String compress) {
    tansferEntireTableSpecificDir(table,directory);
    sqoopOptions.setCompressionCodec(compress);
  }*/
 
 /**********************Unused***************************/
  /* private static void incrementalImport(String table
    , String directory
    , IncrementalMode mode
    , String checkColumn
    , String lastVale) {
    tansferEntireTableSpecificDir(table,directory);
    sqoopOptions.setIncrementalMode(mode);
    sqoopOptions.setAppendMode(true);
    sqoopOptions.setIncrementalTestColumn(checkColumn);
    sqoopOptions.setIncrementalLastValue(lastVale);
  }*/
 
  private static void transferEntireTableSpecificDirHive(String table,
                                                        String directory) {
	  tansferEntireTableSpecificDir(table,directory);
    sqoopOptions.setHiveImport(true);
  }
  
 
  /***************************Unused************************/
  /* private static void transferEntireTableSpecificDirHivePartition(String table,
                                                        String directory
                                                        , String partitionKey
                                                        , String partitionValue) {
    transferEntireTableSpecificDirHive(table,directory);
    sqoopOptions.setHivePartitionKey(partitionKey);
    sqoopOptions.setHivePartitionValue(partitionValue);
  }*/
}
###############################################################################
# Copyright (C) Inferyx Inc, 2018 All rights reserved. 
#
# This unpublished material is proprietary to Inferyx Inc.
# The methods and techniques described herein are considered  trade 
# secrets and/or confidential. Reproduction or distribution, in whole or 
# in part, is forbidden.
#
# Written by Yogesh Palrecha <ypalrecha@inferyx.com>
###############################################################################

# Module sys has to be imported:
import sys  
import pandas as pd
import tensorflow as tf
import numpy as np
from sklearn import preprocessing
import os
import json, codecs
from pyspark.sql.dataframe import DataFrame
from pyspark.sql.catalog import Function
os.environ["PYSPARK_PYTHON"]="/usr/bin/python3"
os.environ["PYSPARK_DRIVER_PYTHON"]="python3"
from pyspark.sql import SparkSession
from keras.wrappers.scikit_learn import KerasClassifier, KerasRegressor
import eli5
from eli5.sklearn import PermutationImportance
from keras.models import model_from_json



print("Inside python script")

paramName = ""
nEpochs=0
seed=0
iterations=0
learningRate=0
optimizationAlgo=""
weightInit=0
updater=""
numInput=0
numOutputs=0
numHidden=0
numLayers=0
activation=""
lossFunction=""
layerNames=""
sourceFilePath=""
modelFilePath=""
targetPath=""
sourceDsType=""
targetDsType=""
tableName=""
operation=""
url=""
isSuccessful = True
hostName=""
dbName=""
userName=""
password=""
port=""
query=""
special_space_replacer=""
inputConfigFilePath=""
otherParams=None
paramList=None
sourceHostName=""
sourceDbName=""
sourcePort=""
sourceUserName=""
sourcePassword=""
targetHostName=""
targetDbName=""
targetPort=""
targetUserName=""
targetPassword=""
sourceDsDetails=None
targetDsDetails=None
targetTableName=""
targetDriver=""
testSetPath=""
rowIdentifier=None
includeFeatures=""
sourceAttrDetails=None
featureAttrDetails=None
sourceQuery=None
inputSourceFileName=None
trainPercent=0.0
testPercent=0.0
output_result = dict() 
outputResultPath=""

# Iteration over all arguments:
plist = ["nEpochs", "seed", "iterations", "learningRate", "optimizationAlgo", "weightInit", "updater", "momentum", "numInput", "numOutputs", "numHidden", "numLayers", "layerNames", "activation", "lossFunction", "sourceFilePath", "modelFilePath", "targetPath", "sourceDsType", "tableName", "operation", "url", "hostName", "dbName", "userName", "password", "query", "special_space_replacer", "port", "otherParams", "sourceHostName", "sourceDbName", "sourcePort", "sourceUserName", "sourcePassword", "targetHostName", "targetDbName" , "targetPort", "targetUserName", "targetPassword", "targetDsType", "targetTableName", "targetDriver", "testSetPath", "includeFeatures", "rowIdentifier",
         "sourceAttrDetails", "featureAttrDetails", "sourceQuery", "inputSourceFileName", "trainPercent", "testPercent", "outputResultPath", "rowIdentifier"]

i = 0
for eachArg in sys.argv:
    print(eachArg)
    if eachArg == "inputConfigFilePath":
        inputConfigFilePath = sys.argv[i+1]
    i = i+1

print("Input Configuration File Path: ", inputConfigFilePath)

with open(inputConfigFilePath, 'r') as json_file:
    input_config = json.load(json_file)

for value in input_config:
    if value == "nEpochs":
        nEpochs = int(input_config[value])
    
    if value == "sourceFilePath":
        sourceFilePath = input_config[value]
    
    if value == "modelFilePath":
        modelFilePath = input_config[value]
    
    if value == "seed":
        seed = input_config[value]
    
    if value == "iterations":
        iterations = input_config[value]
    
    if value == "learningRate":
        learningRate = input_config[value]
    
    if value == "optimizationAlgo":
        optimizationAlgo = input_config[value]
    
    if value == "weightInit":
        weightInit = input_config[value]
    
    if value == "updater":
        updater = input_config[value]
    
    if value == "momentum":
        momentum = input_config[value]
    
    if value == "numInput":
        numInput = int(input_config[value])
    
    if value == "numOutputs":
        numOutputs = input_config[value]
    
    if value == "numHidden":
        numHidden = input_config[value]
    
    if value == "numLayers":
        numLayers = input_config[value]
    
    if value == "layerNames":
        layerNames = input_config[value]
    
    if value == "activation":
        activation = input_config[value]
    
    if value == "lossFunction":
        lossFunction = input_config[value]
    
    if value == "targetPath":
        targetPath = input_config[value]
    
    if value == "sourceDsType":
        sourceDsType = input_config[value]
    
    if value == "tableName":
        tableName = input_config[value]
    
    if value == "operation":
        operation = input_config[value]
    
    if value == "url":
        url = input_config[value]
    
    if value == "hostName":
        hostName = input_config[value]
    
    if value == "dbName":
        dbName = input_config[value]
    
    if value == "userName":
        userName = input_config[value]
    
    if value == "password":
        password = input_config[value]
    
    if value == "query":
        query = input_config[value]
    
    if value == "port":
        port = input_config[value]
    
    if value == "otherParams":
        otherParams = input_config[value]
        
    if value == "sourceDsDetails":
        sourceDsDetails = input_config[value]
    
    if value == "targetDsDetails":
        targetDsDetails = input_config[value]
    
    if value == "targetDsType":
        targetDsType = input_config[value]
    
    if value == "targetTableName":
        targetTableName = input_config[value]

    if value == "testSetPath":
        testSetPath = input_config[value]
        
    if value == "includeFeatures":
        includeFeatures = input_config[value]
        
    if value == "rowIdentifier":
        rowIdentifier = input_config[value]
        
    if value == "trainPercent":
        trainPercent = input_config[value]
        
    if value == "testPercent":
        testPercent = input_config[value]

if otherParams != None:
    for value in otherParams:
        if value == "nEpochs":
            nEpochs = int(otherParams[value])
        
        if value == "seed":
            seed = otherParams[value]
        
        if value == "iterations":
            iterations = otherParams[value]
        
        if value == "learningRate":
            learningRate = otherParams[value]
        
        if value == "sourceAttrDetails":
            sourceAttrDetails = otherParams[value]
        
        if value == "featureAttrDetails":
            featureAttrDetails = otherParams[value]
        
        if value == "sourceQuery":
            sourceQuery = otherParams[value]
        
        if value == "inputSourceFileName":
            inputSourceFileName = otherParams[value]
        
        if value == "outputResultPath":
            outputResultPath = otherParams[value]

if sourceDsDetails != None:
    for value in sourceDsDetails:
        if value == "sourceHostName":
            sourceHostName = sourceDsDetails[value]
        
        if value == "sourceDbName":
            sourceDbName = sourceDsDetails[value]
        
        if value == "sourcePort":
            sourcePort = sourceDsDetails[value]
        
        if value == "sourceUserName":
            sourceUserName = sourceDsDetails[value]
        
        if value == "sourcePassword":
            sourcePassword = sourceDsDetails[value]

if targetDsDetails != None:
    for value in targetDsDetails:
        if value == "targetHostName":
            targetHostName = targetDsDetails[value]
        
        if value == "targetDbName":
            targetDbName = targetDsDetails[value]
        
        if value == "targetPort":
            targetPort = targetDsDetails[value]
        
        if value == "targetUserName":
            targetUserName = targetDsDetails[value]
        
        if value == "targetPassword":
            targetPassword = targetDsDetails[value]
        
        if value == "targetDriver":
            targetDriver = targetDsDetails[value]


print()
print("printing params:")
print("nEpochs: ", nEpochs)
print("seed: ", seed)
print("iterations: ", iterations)
print("learningRate: ", learningRate)
print("optimizationAlgo: ", optimizationAlgo)
print("weightInit: ", weightInit)
print("updater: ", updater)
print("numInput: ", numInput)
print("numOutputs: ", numOutputs)
print("numHidden: ", numHidden)
print("numLayers: ", numLayers)
print("activation: ", activation)
print("lossFunction: ", lossFunction)
print("layerNames: ", layerNames)
print("sourceFilePath: ", sourceFilePath)
print("modelFilePath: ", modelFilePath)
print("targetPath: ", targetPath)
print("sourceDsType: ", sourceDsType)
print("targetDsType: ", targetDsType)
print("tableName: ", tableName)
print("operation: ", operation)
print("url: ", url)
print("hostName: ", hostName)
print("dbName: ", dbName)
print("userName: ", userName)
print("password: ", password)
print("query: ", query)
print("port: ", port)
print("otherParams: ", otherParams)
print("targetTableName: ", targetTableName)
print("testSetPath: ", testSetPath)
print("includeFeatures: ", includeFeatures)
print("rowIdentifier: ", rowIdentifier)
print("sourceAttrDetails: ", sourceAttrDetails)
print("featureAttrDetails: ", featureAttrDetails)
print("sourceQuery: ", sourceQuery)
print("inputSourceFileName: ", inputSourceFileName)
print("trainPercent: ", trainPercent)
print("testPercent: ", testPercent)
print("outputResultPath: ", outputResultPath)

print("sourceHostName: ", sourceHostName)
print("sourceDbName: ", sourceDbName)
print("sourcePort: ", sourcePort)
print("sourceUserName: ", sourceUserName)
print("sourcePassword: ", sourcePassword)

print("targetHostName: ", targetHostName)
print("targetDbName: ", targetDbName)
print("targetPort: ", targetPort)
print("targetUserName: ", targetUserName)
print("targetPassword: ", targetPassword)


# Importing the dataset
dataset = None
def getData(csvPath, dsType, hostName, dbName, port, userName, password, sqlQuery):
    print("inside method getData() >>>>>>>>> dsType : hostName : dbName : port : userName : password :: "+dsType+" : "+hostName+" : "+dbName+" : "+port+" : "+userName+" : "+password)
    if dsType == "file":
        dataset = pd.read_csv(csvPath)
        return dataset
    
    elif dsType == "mysql":
        print("connecting to mysql...")
        import mysql.connector as sql
        db_connection = sql.connect(host=hostName, database=dbName, user=userName, password=password)
        print("connected to mysql...")
        dataset = pd.read_sql(sqlQuery, con=db_connection)
        return dataset
    
    elif dsType == "postgres":
        from sqlalchemy import create_engine
        #    engine = create_engine('postgresql://user@localhost:5432/mydb')
        print("connecting to postgres...")
        engine = create_engine("postgresql://"+userName+"@"+hostName+":"+port+"/"+dbName)
        print("connected to postgres...")
        dataset = pd.read_sql_query(sqlQuery, con=engine)
        return dataset
    
    elif dsType == "oracle":
        import cx_Oracle
        print("connecting to oracle...")
        SID = dbName
        dsn_tns = cx_Oracle.makedsn(hostName, port, SID)
        connection = cx_Oracle.connect(userName, password, dsn_tns)
        print("connected to oracle...")
        dataset = pd.read_sql(sqlQuery, con=connection)
        return dataset
    
    elif dsType == "hive":
        from pyhive import hive
        print("connecting to hive...")
        connection = hive.Connection(host=hostName, port=port, auth='NONE', username=userName, database=dbName)
        print("connected to hive...")
        dataset = pd.read_sql(sqlQuery, con=connection)
        sparkSession = SparkSession.builder.appName('pandasToSparkDF').getOrCreate()
        
        # import pyhs2
        # conn = pyhs2.connect(host=hostName, port=port, authMechanism="PLAIN", user=userName, password=password, database=dbName)
        # cur = conn.cursor()
        # cur.execute(query)
        # #Return column info from query
        # if cur.getSchema() is None:
        #     cur.close()
        #     conn.close()
        #     return None
    
        # columnNames = [a['columnName'] for a in  cur.getSchema()] 
        # print (columnNames)
        # dataset =  pd.DataFrame(cur.fetch(),columns=columnNames)   
    
        # cur.close()
        # conn.close()
        
        return dataset
    
    elif dsType == "impala":
        import ibis
        #    hdfs = ibis.hdfs_connect(host=os.environ['IP_HDFS'], port=50070)
        #    client = ibis.impala.connect(host=os.environ['IP_IMPALA'], port=21050, hdfs_client=hdfs)
        #    hdfs = ibis.hdfs_connect(host=hostName, port=50070)
        print("connecting to impala...")
        client = ibis.impala.connect(host=hostName, port=port, auth_mechanism='PLAIN',user=userName, password=password)
        print("connected to impala...")
        requete = client.sql(sqlQuery)
        dataset = requete.execute(limit=None)
        return dataset
        #    from impala.dbapi import connect
        #    conn = connect( host=hostName, port=port, auth_mechanism='PLAIN', kerberos_service_name='impala')
        #    cur = conn.cursor()
        #    cur.execute('SHOW TABLES')
        #    cur.fetchall()
        
def addIndexToSparkDf(spark_df: DataFrame):
    from pyspark.sql.window import Window
    from pyspark.sql.functions import row_number
#     
    w = Window().orderBy(spark_df.columns[len(spark_df.columns)-1])
    
    indexed_spark_df =  spark_df.withColumn("rowNum", row_number().over(w))  
    return indexed_spark_df

def joinSparkDfByIndex(dfLHS, dfRHS):
    joined_df = dfLHS.join(dfRHS,["rowNum"])
    return joined_df

def createSparkDfByPandasDfAndSparkSchema(pd_df, schema):  
    sparkSession = SparkSession.builder.appName('pandasToSparkDF').getOrCreate()
    spark_df = sparkSession.createDataFrame(pd_df, schema)
    return spark_df

def getSparkSchemaByDtypes(pd_dtypes):
    from pyspark.sql.types import StructType
    from pyspark.sql.types import StructField
    
    fields = [StructField(field_name, getSparkDataType(pd_dtypes.get_value(field_name).name), True) for field_name in pd_dtypes.keys()]
    return StructType(fields)

def getSparkDataType(other_datatype):
    from pyspark.sql.types import DoubleType, IntegerType, StringType, FloatType
    if(other_datatype.__contains__("int")):
        return IntegerType()
    elif(other_datatype.__contains__("double")):
        return DoubleType()
    elif(other_datatype.__contains__("float")):
        return DoubleType()
    elif(other_datatype.__contains__("string")):
        return StringType()
    elif(other_datatype.__contains__("object")):
        return StringType()
    

#saving train test result
def saveSparkDf(spark_df, testSetPath):
    print("saving prediction result into path "+testSetPath+"...")
    spark_df.write.save(testSetPath, format="parquet")
    
    
def generatePredictStatus(spark_df: DataFrame):
    from pyspark.sql import Row
    from pyspark.sql.types import StructType, StructField, StringType
    
    schema = StructType([StructField("prediction_status", StringType(), True)])
    sparkSession = SparkSession.builder.appName('pandasToSparkDF').getOrCreate()
    mapped_row = spark_df.rdd.map(lambda x: Row(prepareStatus(x[1], x[2])))
    prediction_status_df = sparkSession.createDataFrame(mapped_row, schema)
    
    return prediction_status_df

def prepareStatus(label_val, prediction_val):
    if prediction_val > 0.49500:
        prediction_val = 1.0
    else:
        prediction_val = 0.0
        
    if label_val == prediction_val:
        return "true"
    else:
        return "false"
    
def model():
    import keras
    from keras.models import Sequential
    from keras.layers import Dense
    
    # Initialising the ANN
    classifier = Sequential()
    
    # Adding the input layer and the first hidden layer
    classifier.add(Dense(output_dim = 6, init = 'uniform', activation = 'relu', input_dim = numInput))
    
    # Adding the second hidden layer
    classifier.add(Dense(output_dim = 6, init = 'uniform', activation = 'relu'))
    
    # Adding the output layer
    classifier.add(Dense(output_dim = 1, init = 'uniform', activation = 'sigmoid'))
    
    # Compiling the ANN
    classifier.compile(optimizer = 'adam', loss = 'binary_crossentropy', metrics = ['accuracy'])
    
    return classifier
    
#train operation
def train():
    # Encoding categorical data
    dataset = getData(sourceFilePath, sourceDsType, sourceHostName, sourceDbName, sourcePort, sourceUserName, sourcePassword, query)
    
    print("total_size: ", len(dataset))    
    output_result["total_size"]=len(dataset)
    
    from sklearn.preprocessing import LabelEncoder, OneHotEncoder
    labelencoder_X_1 = LabelEncoder()
    dataset.iloc[0] = labelencoder_X_1.fit_transform(dataset.iloc[0])
    print('label encoding done')
    X = dataset.iloc[:, 1:].values
    y = dataset.iloc[:, 0].values
    
    print(X)
    print(y)
    
    # Splitting the dataset into the Training set and Test set
    from sklearn.model_selection import train_test_split
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = testPercent, random_state = 0)
    
    # Feature Scaling
    from sklearn.preprocessing import StandardScaler
    sc = StandardScaler()
    X_train = sc.fit_transform(X_train)
    X_test = sc.transform(X_test)
        
    print("train_size: ", len(X_train))
    print("test_size: ", len(X_test))
    
    output_result["train_size"]=len(X_train)
    output_result["test_size"]=len(X_test)
        
    print("Importing the Keras libraries and packages")
    # Importing the Keras libraries and packages
#     import keras
#     from keras.models import Sequential
#     from keras.layers import Dense
#     from keras.models import model_from_json
#     
#     # Initialising the ANN
#     classifier = Sequential()
#     
#     # Adding the input layer and the first hidden layer
#     classifier.add(Dense(output_dim = 6, init = 'uniform', activation = 'relu', input_dim = numInput))
#     
#     # Adding the second hidden layer
#     classifier.add(Dense(output_dim = 6, init = 'uniform', activation = 'relu'))
#     
#     # Adding the output layer
#     classifier.add(Dense(output_dim = 1, init = 'uniform', activation = 'sigmoid'))
#     
#     # Compiling the ANN
#     classifier.compile(optimizer = 'adam', loss = 'binary_crossentropy', metrics = ['accuracy'])
    
    
    kerasClassifier = KerasClassifier(build_fn=model, batch_size = 10, nb_epoch = nEpochs, verbose=0)    
    kerasClassifier.fit(X_train, y_train, batch_size = 10, nb_epoch = nEpochs)

    permutationImportance = PermutationImportance(kerasClassifier, random_state=1).fit(X_train, y_train)
    featureImportance = permutationImportance.feature_importances_
    print("feature importance type: ", type(featureImportance))
    print("feature importances: ", featureImportance)
    
    classifier = model()
    # Fitting the ANN to the Training set
    classifier.fit(X_train, y_train, batch_size = 10, nb_epoch = nEpochs)
    
    score = classifier.evaluate(X_test, y_test, batch_size=10)

    print("model_score: ", score)    
    output_result["score"]=score
    
    # serialize model to JSON
    model_json = classifier.to_json()
    with open(modelFilePath+".spec", "w") as json_file:
        json_file.write(model_json)
    # serialize weights to HDF5
    classifier.save_weights(modelFilePath+".h5")
    print("Saved model to disk")    
    
    # later...
     
    # load json and create model
    json_file = open(modelFilePath+".spec", 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    # load weights into new model
    loaded_model.load_weights(modelFilePath+".h5")
    print("Loaded model from disk")
    
    
    # Part 3 - Making the predictions and evaluating the model
    y_pred = loaded_model.predict(X_test) 
    
    #creating prediction dataframe
    pred_pd_df = pd.DataFrame(y_pred)
    from pyspark.sql.types import StructType, StructField, DoubleType
    spark_pred_df = createSparkDfByPandasDfAndSparkSchema(pred_pd_df, StructType([StructField("prediction", DoubleType(), True)]))
    spark_pred_df = addIndexToSparkDf(spark_pred_df)
    
    #creating label dataframe
    pd_label_df = pd.DataFrame(y_test)
    spark_label_df = createSparkDfByPandasDfAndSparkSchema(pd_label_df, StructType([StructField("label", DoubleType(), True)]))
    spark_label_df = addIndexToSparkDf(spark_label_df)
    
    #joining prediction and label dataframes
    test_result_spark_df = joinSparkDfByIndex(spark_label_df, spark_pred_df)
    
    #calculating prediction status and generating spark dataframe from it
    spark_pred_status_df = generatePredictStatus(test_result_spark_df)
    spark_pred_status_df = addIndexToSparkDf(spark_pred_status_df)
       
    #joining prediction status dataframe with joined dataframe of label and test prediction result    
    test_result_spark_df = joinSparkDfByIndex(test_result_spark_df, spark_pred_status_df)
    
    joined_df = None
    if includeFeatures == "Y":
        feature_schema = getSparkSchemaByDtypes(dataset.iloc[:, 1:].dtypes)
    
        pd_X_test = pd.DataFrame(X_test)
        spark_X_test_df = createSparkDfByPandasDfAndSparkSchema(pd_X_test, feature_schema)
        spark_X_test_df = addIndexToSparkDf(spark_X_test_df)
        joined_df = joinSparkDfByIndex(spark_X_test_df, test_result_spark_df)
    else:
        joined_df = test_result_spark_df
          
    if(rowIdentifier != None and (inputSourceFileName != None and inputSourceFileName != "")):
        sourceDataset = getData(inputSourceFileName, sourceDsType, sourceHostName, sourceDbName
                                , sourcePort, sourceUserName, sourcePassword, sourceQuery)
       
        src_train, src_test = train_test_split(sourceDataset.iloc[:, :].values, test_size = testPercent, random_state = 0)
        
        source_schema = getSparkSchemaByDtypes(sourceDataset.iloc[:, :].dtypes)        
        pd_scr_test = pd.DataFrame(src_test)
        spark_src_test_df = createSparkDfByPandasDfAndSparkSchema(pd_scr_test, source_schema)
        spark_src_test_df = addIndexToSparkDf(spark_src_test_df)
        
        joined_df = joinSparkDfByIndex(spark_src_test_df, joined_df)

    #removing index column
    joined_df = joined_df.drop("rowNum")
        
    #saving converted dataframe
    if testSetPath != None:
        saveSparkDf(joined_df, testSetPath)
    
    y_pred = (y_pred > 0.5)    
    print(y_pred)
    
    # Making the Confusion Matrix
    from sklearn.metrics import confusion_matrix, accuracy_score, precision_score, recall_score, roc_auc_score, f1_score
    cm = confusion_matrix(y_test, y_pred)
    accuracy = accuracy_score(y_test, y_pred)
    precision = precision_score(y_test, y_pred)
    recall = recall_score(y_test, y_pred)
    roc_auc = roc_auc_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred)
    
    output_result["accuracy"]=accuracy
    output_result["precision"]=precision
    output_result["recall"]=recall
    output_result["roc_auc"]=roc_auc
    output_result["f1"]=f1
    output_result["confusion_matrix"]=cm.tolist()
    output_result["featureImportance"]=featureImportance.tolist()
            
    print("saving train output into path '", outputResultPath+".json", "'")
    with codecs.open(outputResultPath+".json", 'w', 'utf8') as f:
         f.write(json.dumps(str(output_result), sort_keys = True, ensure_ascii=False))    
    
    print("accuracy: ", accuracy)
    print("precision: ", precision)    
    print("recall: ", recall)    
    print("roc_auc: ", roc_auc)    
    print("f1: ", f1)    
    print("confusion_matrix: ", cm)
       
    return isSuccessful

#prediction operation
def predict():
    # Importing the dataset
    #    dataset = pd.read_csv(sourceFilePath)
    dataset2 = getData(sourceFilePath, sourceDsType, sourceHostName, sourceDbName, sourcePort, sourceUserName, sourcePassword, query)
    
    print("predict dataset size: ", len(dataset2))
    
    # Feature Scaling
    from sklearn.preprocessing import StandardScaler
    from keras.models import model_from_json
    
    sc = StandardScaler()
    feature_dataset = sc.fit_transform(dataset2)
    
    print("Data to be predicted:")
    print(feature_dataset)
    
    # load json and create model
    print("Loading model from disk")
    json_file = open(modelFilePath+".spec", 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    # load weights into new model
    loaded_model.load_weights(modelFilePath+".h5")
    print("Loaded model from disk")
    
    # Predicting the results
    result_pred = loaded_model.predict(feature_dataset)
    
    #converting predicted dataframe to panda dataframe
    pred_pd_df = pd.DataFrame(result_pred)
    
    #saving converted dataframe
    from pyspark.sql.types import DoubleType
    from pyspark.sql.types import StructType
    from pyspark.sql.types import StructField
   
    print("prediction result:")  
    spark_pred_df = createSparkDfByPandasDfAndSparkSchema(pred_pd_df, StructType([StructField("prediction", DoubleType(), True)]))  
    spark_pred_df = addIndexToSparkDf(spark_pred_df)
    spark_pred_df.show(20, False)
    
    joined_df = None
    if includeFeatures == "Y":
        feature_schema = getSparkSchemaByDtypes(dataset2.iloc[:, :].dtypes)
    
        pd_feature_dataset = pd.DataFrame(feature_dataset)
        spark_feature_dataset_df = createSparkDfByPandasDfAndSparkSchema(pd_feature_dataset, feature_schema)
        spark_feature_dataset_df = addIndexToSparkDf(spark_feature_dataset_df)
        joined_df = joinSparkDfByIndex(spark_feature_dataset_df, spark_pred_df)
    else:
        joined_df = spark_pred_df       
    
    if(rowIdentifier != None and (inputSourceFileName != None and inputSourceFileName != "")):
        sourceDataset = getData(inputSourceFileName, sourceDsType, sourceHostName, sourceDbName
                                , sourcePort, sourceUserName, sourcePassword, sourceQuery)
                
        source_schema = getSparkSchemaByDtypes(sourceDataset.iloc[:, :].dtypes)        
        pd_source_df = pd.DataFrame(sourceDataset)
        spark_source_df = createSparkDfByPandasDfAndSparkSchema(pd_source_df, source_schema)
        spark_source_df = addIndexToSparkDf(spark_source_df)
                
        joined_df = joinSparkDfByIndex(spark_source_df, joined_df)
        
    #removing index column
    joined_df = joined_df.drop("rowNum")
        
    print("saving prediction result into "+targetDsType+"...")
    #saving converted dataframe    
    if targetDsType == 'file':
        joined_df.write.save(targetPath, format="parquet")
    elif  targetDsType == 'hive' or targetDsType == 'impala':
        joined_df.write.insertInto(targetDbName+"."+targetTableName)
    else:
        joined_df.repartition(10).write.mode('append').options().jdbc(url, targetTableName, properties={"user": targetUserName, "password": targetPassword, "driver": targetDriver})
#      
    return isSuccessful

#calling method as per operation
if operation == "train":
    result = train()
    if result:
        print("Successfull operation: "+"Training")
    else:
        print("Unsuccessfull operation: "+"Training")
elif operation == "predict":
    result = predict()
    if result:
        print("Successfull operation: "+"prediction")
    else:
        print("Unsuccessfull operation: "+"prediction")

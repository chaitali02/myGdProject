# Module sys has to be imported:
import sys  
import pandas as pd
import tensorflow as tf
import numpy as np
from sklearn import preprocessing
import os
import json
os.environ["PYSPARK_PYTHON"]="/usr/bin/python3"
os.environ["PYSPARK_DRIVER_PYTHON"]="python3"
from pyspark.sql import SparkSession


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


# Iteration over all arguments:

plist = ["nEpochs", "seed", "iterations", "learningRate", "optimizationAlgo", "weightInit", "updater", "momentum", "numInput", "numOutputs", "numHidden", "numLayers", "layerNames", "activation", "lossFunction", "sourceFilePath", "modelFilePath", "targetPath", "sourceDsType", "tableName", "operation", "url", "hostName", "dbName", "userName", "password", "query", "special_space_replacer", "port", "otherParams", "sourceHostName", "sourceDbName", "sourcePort", "sourceUserName", "sourcePassword", "targetHostName", "targetDbName" , "targetPort", "targetUserName", "targetPassword", "targetDsType", "targetTableName", "targetDriver"]

i = 0
for eachArg in sys.argv:
    print(eachArg)
    if eachArg == "inputConfigFilePath":
    	inputConfigFilePath = sys.argv[i+1]
    i = i+1

print("Input Configuration File Path:")
print(inputConfigFilePath)

with open(inputConfigFilePath, 'r') as json_file:
    input_config = json.load(json_file)

for value in input_config:
    print(value)
    
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
print(nEpochs)
print(seed)
print(iterations)
print(learningRate)
print(optimizationAlgo)
print(weightInit)
print(updater)
print(numInput)
print(numOutputs)
print(numHidden)
print(numLayers)
print(activation)
print(lossFunction)
print(layerNames)
print(sourceFilePath)
print(modelFilePath)
print(targetPath)
print(sourceDsType)
print(targetDsType)
print(tableName)
print(operation)
print(url)
print(hostName)
print(dbName)
print(userName)
print(password)
print(query)
print(port)
print(otherParams)
print(targetTableName)

print(sourceHostName)
print(sourceDbName)
print(sourcePort)
print(sourceUserName)
print(sourcePassword)

print(targetHostName)
print(targetDbName)
print(targetPort)
print(targetUserName)
print(targetPassword)

# Importing the dataset
dataset = None
def getData(dsType, hostName, dbName, port, userName, password):
    print("getData method() :: dsType : hostName : dbName : port : userName : password :: "+dsType+" : "+hostName+" : "+dbName+" : "+port+" : "+userName+" : "+password)
    if dsType == "file":
    	dataset = pd.read_csv(sourceFilePath)
    	return dataset
    
    elif dsType == "mysql":
        print("connecting to mysql...")
        import mysql.connector as sql
        db_connection = sql.connect(host=hostName, database=dbName, user=userName, password=password)
        print("connected to mysql...")
        dataset = pd.read_sql(query, con=db_connection)
        return dataset
    
    elif dsType == "postgres":
        from sqlalchemy import create_engine
        #	engine = create_engine('postgresql://user@localhost:5432/mydb')
        print("connecting to postgres...")
        engine = create_engine("postgresql://"+userName+"@"+hostName+":"+port+"/"+dbName)
        print("connected to postgres...")
        dataset = pd.read_sql_query(query, con=engine)
        return dataset
    
    elif dsType == "oracle":
        import cx_Oracle
        print("connecting to oracle...")
        SID = dbName
        dsn_tns = cx_Oracle.makedsn(hostName, port, SID)
        connection = cx_Oracle.connect(userName, password, dsn_tns)
        print("connected to oracle...")
        dataset = pd.read_sql(query, con=connection)
        return dataset
    
    elif dsType == "hive":
        from pyhive import hive
        print("connecting to hive...")
        connection = hive.Connection(host=hostName, port=port, auth='NONE', username=userName, database=dbName)
        print("connected to hive...")
        dataset = pd.read_sql(query, con=connection)
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
        #	hdfs = ibis.hdfs_connect(host=os.environ['IP_HDFS'], port=50070)
        #	client = ibis.impala.connect(host=os.environ['IP_IMPALA'], port=21050, hdfs_client=hdfs)
        #	hdfs = ibis.hdfs_connect(host=hostName, port=50070)
        print("connecting to impala...")
        client = ibis.impala.connect(host=hostName, port=port, auth_mechanism='PLAIN',user=userName, password=password)
        print("connected to impala...")
        requete = client.sql(query)
        dataset = requete.execute(limit=None)
        return dataset
        #	from impala.dbapi import connect
        #	conn = connect( host=hostName, port=port, auth_mechanism='PLAIN', kerberos_service_name='impala')
        #	cur = conn.cursor()
        #	cur.execute('SHOW TABLES')
        #	cur.fetchall()


#train operation
def train():
    # Encoding categorical data
    dataset = getData(sourceDsType, sourceHostName, sourceDbName, sourcePort, sourceUserName, sourcePassword)
    
    print("   ")
    print("total_size")
    print(dataset.size)
    print("  ")
    
    # if dataset != None:
    #     print("data obtained:")
    #     print(dataset)
    # else:
    # 	raise Exception('No data found.')
    
    from sklearn.preprocessing import LabelEncoder, OneHotEncoder
    labelencoder_X_1 = LabelEncoder()
    dataset.iloc[0] = labelencoder_X_1.fit_transform(dataset.iloc[0])
    #labelencoder_X_2 = LabelEncoder()
    #dataset['pin_number'] = labelencoder_X_2.fit_transform(dataset['pin_number'])
    print('label encoding done')
    #X = dataset.iloc[:, 1:-2].values
    #y = dataset.iloc[:, -1].values
    X = dataset.iloc[:, 1:(numInput+1)].values
    y = dataset.iloc[:, 0].values
    
    #print("printing X:")
    print(X)
    #print("printing y:")
    print(y)
    
    # Splitting the dataset into the Training set and Test set
    from sklearn.model_selection import train_test_split
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.2, random_state = 0)
    
    # Feature Scaling
    from sklearn.preprocessing import StandardScaler
    sc = StandardScaler()
    X_train = sc.fit_transform(X_train)
    X_test = sc.transform(X_test)
    
    print("   ")
    print("train_size")
    print(X_train.size)
    print("  ")
    print("   ")
    print("test_size")
    print(X_test.size)
    print("  ")
    
    
    #print("printing X_train:")
    print(X_train)
    #print("printing X_test:")
    print(X_test)
    
    print("Importing the Keras libraries and packages")
    # Importing the Keras libraries and packages
    import keras
    from keras.models import Sequential
    from keras.layers import Dense
    from keras.models import model_from_json
    
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
    
    # Fitting the ANN to the Training set
    classifier.fit(X_train, y_train, batch_size = 10, nb_epoch = nEpochs)
    
    score = classifier.evaluate(X_test, y_test, batch_size=10)

    print("   ")
    print("model score:")
    print(score)
    print("  ")
    
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
    
    # Predicting the Test set results
    y_pred = loaded_model.predict(X_test)
    print(y_pred)
    
    y_pred = (y_pred > 0.5)
    #if y_pred.all() == "true":
    #    y_pred[:] = 1
    #else:
    #    y_pred[:] = 0
    
    #for col in y_pred.columns:
    #   if (y_pred.all() == true):
    #        y_pred[col].values[:] = 1
    #    else:
    #        y_pred[col].values[:] = 0
    
    print(y_pred)
    
    # Making the Confusion Matrix
    from sklearn.metrics import confusion_matrix, accuracy_score, precision_score, recall_score, roc_auc_score, f1_score
    cm = confusion_matrix(y_test, y_pred)
    accuracy = accuracy_score(y_test, y_pred)
    precision = precision_score(y_test, y_pred)
    recall = recall_score(y_test, y_pred)
    roc_auc = roc_auc_score(y_test, y_pred)
    f1 = f1_score(y_test, y_pred)
    
    print("   ")
    print("accuracy")
    print(accuracy)
    print("  ")
    
    print("   ")
    print("precision")
    print(precision)
    print("  ")
    
    print("   ")
    print("recall")
    print(recall)
    print("  ")
    
    print("   ")
    print("roc_auc")
    print(roc_auc)
    print("  ")
    
    print("   ")
    print("f1")
    print(f1)
    print("  ")
    
    print("   ")
    print("confusion mattrix:")
    print(cm)
    print("  ")
    return isSuccessful

#prediction operation
def predict():
    # Importing the dataset
    #	dataset = pd.read_csv(sourceFilePath)
    dataset2 = getData(sourceDsType, sourceHostName, sourceDbName, sourcePort, sourceUserName, sourcePassword).iloc[:, 1:(numInput+1)].values
    print(dataset2)
    
    # Feature Scaling
    from sklearn.preprocessing import StandardScaler
    from keras.models import model_from_json
    
    sc = StandardScaler()
    pred_dataset = sc.fit_transform(dataset2)
    
    print("Data to be predicted:")
    print(pred_dataset)
    
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
    result_pred = loaded_model.predict(pred_dataset)
    
    #converting predicted dataframe to panda dataframe
    pred_pd_df = pd.DataFrame(result_pred)
    
    #saving converted dataframe
    from pyspark.sql.types import DoubleType
    from pyspark.sql.types import StructType
    from pyspark.sql.types import StructField
    
    # sparkSession = SparkSession.builder.appName('pandasToSparkDF').config('spark.driver.extraClassPath','/home/rohini/Desktop/mysql_connector/mysql-connector-java_8.0.13-1ubuntu16.04_all.deb').config('spark.executor.extraClassPath','/home/rohini/Desktop/mysql_connector/mysql-connector-java_8.0.13-1ubuntu16.04_all.deb').getOrCreate()
    sparkSession = SparkSession.builder.appName('pandasToSparkDF').getOrCreate()
    pred_pd_df = sparkSession.createDataFrame(pred_pd_df, StructType([StructField("prediction", DoubleType(), True)]))
    print("prediction result:")	
    pred_pd_df.show(20, False)
    
    print("saving prediction result into "+targetDsType+"...")
    if targetDsType == 'file':
        pred_pd_df.write.save(targetPath, format="parquet")
    elif  targetDsType == 'hive' or targetDsType == 'impala':
        pred_pd_df.write.insertInto(targetDbName+"."+targetTableName)
    else:
         pred_pd_df.repartition(10).write.mode('append').options().jdbc(url, targetTableName, properties={"user": targetUserName, "password": targetPassword, "driver": targetDriver})
#        pred_pd_df.write.format('jdbc').options(url=url, driver=targetDriver, dbtable=targetTableName, user=targetUserName, password=targetPassword).mode('append').save()
        # connectionProperties = {"user" : targetUserName, "password" : targetPassword, "driver" : targetDriver}
        # val connectionProperties = new Properties()
        # connectionProperties.put("user", targetUserName)
        # connectionProperties.put("password", targetPassword)
        # connectionProperties.put("driver", targetDriver)
    
    #saving converted dataframe
    #engine can be of the type: auto/pyarrow/fastparquet
    #	pred_pd_df.to_parquet(targetPath+".parquet", engine='auto')
    #	print(pred_pd_df)
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

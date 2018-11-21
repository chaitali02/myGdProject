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
savePredict=""
dsType=""
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


# Iteration over all arguments:

plist = ["nEpochs", "seed", "iterations", "learningRate", "optimizationAlgo", "weightInit", "updater", "momentum", "numInput", "numOutputs", "numHidden", "numLayers", "layerNames", "activation", "lossFunction", "sourceFilePath", "modelFilePath", "savePredict", "dsType", "tableName", "operation", "url", "hostName", "dbName", "userName", "password", "query", "special_space_replacer", "port", "otherParams"]

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

        if value == "savePredict":
            savePredict = input_config[value]

        if value == "dsType":
            dsType = input_config[value]

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

for value in otherParams:
	if value == "nEpochs":
            nEpochs = int(otherParams[value])

	if value == "seed":
            seed = otherParams[value]

	if value == "iterations":
            iterations = otherParams[value]

	if value == "learningRate":
            learningRate = otherParams[value]

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
print(savePredict)
print(dsType)
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

# Importing the dataset
dataset = None
if dsType == "file":
	dataset = pd.read_csv(sourceFilePath)

elif dsType == "mysql":	
	import mysql.connector as sql
	db_connection = sql.connect(host=hostName, database=dbName, user=userName, password=password)
	dataset = pd.read_sql(query, con=db_connection)

elif dsType == "postgres":
	from sqlalchemy import create_engine
#	engine = create_engine('postgresql://user@localhost:5432/mydb')
	engine = create_engine("postgresql://"+userName+"@"+hostName+":"+port+"/"+dbName)
	dataset = pd.read_sql_query(query, con=engine)

elif dsType == "postgres":
	import cx_Oracle
	SID = dbName
	dsn_tns = cx_Oracle.makedsn(hostName, port, SID)
	connection = cx_Oracle.connect(userName, password, dsn_tns)
	dataset = pd.read_sql(query, con=connection)

elif dsType == "hive":
	from pyhive import hive
	connection = hive.Connection(host=hostName, port=port, auth='NOSASL', username=hostName, database=dbName)
	dataset = pd.read_sql(query, con=connection)

elif dsType == "impala":
	import ibis
#	hdfs = ibis.hdfs_connect(host=os.environ['IP_HDFS'], port=50070)
#	client = ibis.impala.connect(host=os.environ['IP_IMPALA'], port=21050, hdfs_client=hdfs)
	hdfs = ibis.hdfs_connect(host=hostName, port=50070)
	client = ibis.impala.connect(host=hostName, port=port, hdfs_client=hdfs)
	requete = client.sql(query)
	dataset = requete.execute(limit=None)

#train operation
def train():
	# Encoding categorical data
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
	print("model score:")
	print(score)
	print(" ")

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
	from sklearn.metrics import confusion_matrix
	cm = confusion_matrix(y_test, y_pred)
	print("confusion mattrix:")
	print(cm)
	print(" ")
	return isSuccessful

#prediction operation
def predict():
	# Importing the dataset
#	dataset = pd.read_csv(sourceFilePath)
	dataset2 = dataset.iloc[:, 1:(numInput+1)].values

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

	spark = SparkSession.builder.appName('pandasToSparkDF').getOrCreate()
	pred_pd_df = spark.createDataFrame(pred_pd_df, StructType([StructField("prediction", DoubleType(), True)]))
	print("prediction result:")	
	pred_pd_df.show(20, False)
	
	print("saving prediction result at: "+savePredict)
	pred_pd_df.write.save(savePredict, format="parquet")		

	#saving converted dataframe
	#engine can be of the type: auto/pyarrow/fastparquet
#	pred_pd_df.to_parquet(savePredict+".parquet", engine='auto')
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

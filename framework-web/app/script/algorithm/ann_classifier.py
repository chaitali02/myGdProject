# Module sys has to be imported:
import sys  
import pandas as pd
import tensorflow as tf
import numpy as np
from sklearn import preprocessing
import os
os.environ["PYSPARK_PYTHON"]="/usr/bin/python3"
os.environ["PYSPARK_DRIVER_PYTHON"]="python3"
from pyspark.sql import SparkSession


print("Inside python training script")              

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
fileName=""
modelFileName=""
savePredict=""
dsType=""
tableName=""
operation=""
url=""

# Iteration over all arguments:

plist = ["nEpochs", "seed", "iterations", "learningRate", "optimizationAlgo", "weightInit", "updater", "momentum", "numInput", "numOutputs", "numHidden", "numLayers", "layerNames", "activation", "lossFunction", "filename", "modelFileName", "savePredict", "dsType", "tableName", "operation", "url"]

for eachArg in sys.argv:   
        print(eachArg)
        if paramName == "nEpochs":
            nEpochs = int(eachArg)
        if paramName == "filename":
            fileName = eachArg
        if paramName == "modelFileName":
            modelFileName = eachArg
        if paramName == "seed":
            seed = eachArg
        if paramName == "iterations":
            iterations = eachArg
        if paramName == "learningRate":
            learningRate = eachArg
        if paramName == "optimizationAlgo":
            optimizationAlgo = eachArg
        if paramName == "weightInit":
            weightInit = eachArg
        if paramName == "updater":
            updater = eachArg
        if paramName == "momentum":
            momentum = eachArg
        if paramName == "numInput":
            numInput = int(eachArg)
        if paramName == "numOutputs":
            numOutputs = eachArg
        if paramName == "numHidden":
            numHidden = eachArg
        if paramName == "numLayers":
            numLayers = eachArg
        if paramName == "layerNames":
            layerNames = eachArg
        if paramName == "activation":
            activation = eachArg
        if paramName == "lossFunction":
            lossFunction = eachArg
        if paramName == "savePredict":
            savePredict = eachArg
        if paramName == "dsType":
            dsType = eachArg
        if paramName == "tableName":
            tableName = eachArg
        if paramName == "operation":
            operation = eachArg
        if paramName == "url":
            url = eachArg

        if eachArg in plist:
            paramName=eachArg
        else:
            paramName=""
	
        
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
print(fileName)
print(modelFileName)
print(savePredict)
print(dsType)
print(tableName)
print(operation)
print(url)

#train operation
def train():
	# Importing the dataset
	dataset = pd.read_csv(fileName)

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
	y = dataset.iloc[:, (numInput+1)].values

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

	# serialize model to JSON
	model_json = classifier.to_json()
	with open(modelFileName+".json", "w") as json_file:
	    json_file.write(model_json)
	# serialize weights to HDF5
	classifier.save_weights(modelFileName+".h5")
	print("Saved model to disk")



	# later...
	 
	# load json and create model
	json_file = open(modelFileName+".json", 'r')
	loaded_model_json = json_file.read()
	json_file.close()
	loaded_model = model_from_json(loaded_model_json)
	# load weights into new model
	loaded_model.load_weights(modelFileName+".h5")
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
	print(cm)
	return true

#prediction operation
def predict():
	# Importing the dataset
	dataset = pd.read_csv(fileName)

	# Feature Scaling
	from sklearn.preprocessing import StandardScaler
	from keras.models import model_from_json

	sc = StandardScaler()
	pred_dataset = sc.fit_transform(dataset)

	print(pred_dataset)

	# load json and create model
	json_file = open(modelFileName+".json", 'r')
	loaded_model_json = json_file.read()
	json_file.close()
	loaded_model = model_from_json(loaded_model_json)
	# load weights into new model
	loaded_model.load_weights(modelFileName+".h5")
	print("Loaded model from disk")

	# Predicting the results
	result_pred = loaded_model.predict(pred_dataset)
	print(result_pred)

	#converting predicted dataframe to panda dataframe
	pred_pd_df = pd.DataFrame(result_pred)
#	pred_pd_df = pred_pd_df.astype(str)

	#saving converted dataframe
	spark = SparkSession.builder.appName('pandasToSparkDF').getOrCreate()
	pred_pd_df = spark.createDataFrame(pred_pd_df)
	pred_pd_df.write.save(savePredict, format="parquet")	
#	pred_pd_df.show()	

	#saving converted dataframe
	#engine can be of the type: auto/pyarrow/fastparquet
#	pred_pd_df.to_parquet(savePredict+".parquet", engine='auto')
#	print(pred_pd_df)
	return true

#calling method as per operation
if operation == "train":
	train()
elif operation == "predict":
	predict()

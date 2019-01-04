# Module sys has to be imported:
import sys  
import pandas as pd
import tensorflow as tf
import numpy as np
from sklearn import preprocessing

print("Inside python predict script")              

paramName = ""
fileName=""
modelFileName=""
savePredict=""

# Iteration over all arguments:
plist = ["filename", "modelFileName", "savePredict"]
for eachArg in sys.argv:   
        print(eachArg)
        if paramName == "savePredict":
            nEpochs = eachArg
        if paramName == "filename":
            fileName = eachArg
        if paramName == "modelFileName":
            modelFileName = eachArg
        if eachArg in plist:
            paramName=eachArg
        else:
            paramName=""
	
  
print(fileName)
print(modelFileName)
print(savePredict)

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

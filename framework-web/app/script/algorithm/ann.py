#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Sat Nov 10 23:40:38 2018

@author: joy
"""

import pandas as pd
import tensorflow as tf
import numpy as np
from sklearn import preprocessing
import sys

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
fileName=""
modelFileName=""
# Iteration over all arguments:
for eachArg in sys.argv:   
        print(eachArg)
        if paramName == "nEpochs":
            nEpochs = int(eachArg)
        if paramName == "fileName":
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
            numInput = eachArg
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
        
        if eachArg in ["nEpochs", "seed", "iterations", "learningRate", "optimizationAlgo", "weightInit", "updater", "momentum", "numInput", "numOutputs", "numHidden", "numLayers", "layerNames", "activation", "lossFunction", "fileName", "modelFileName"]:
            paramName = eachArg
        else:
            paramName = ""
        
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
# Importing the dataset
dataset = pd.read_csv(fileName)

# Encoding categorical data
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
labelencoder_X_1 = LabelEncoder()
dataset['account_type_id'] = labelencoder_X_1.fit_transform(dataset['account_type_id'])
labelencoder_X_2 = LabelEncoder()
dataset['pin_number'] = labelencoder_X_2.fit_transform(dataset['pin_number'])
print('label encoding done')
X = dataset.iloc[:, 3:6].values
y = dataset.iloc[:, 2].values

print(X)
print(y)
#X[:, 1] = labelencoder_X_1.fit_transform(X[:, 1])
#labelencoder_X_2 = LabelEncoder()
#X[:, 2] = labelencoder_X_2.fit_transform(X[:, 2])
#onehotencoder = OneHotEncoder(categorical_features = [1])
#X = onehotencoder.fit_transform(X).toarray()
#X = X[:, 1:]


# Splitting the dataset into the Training set and Test set
from sklearn.model_selection import train_test_split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size = 0.2, random_state = 0)

# Feature Scaling
from sklearn.preprocessing import StandardScaler
sc = StandardScaler()
X_train = sc.fit_transform(X_train)
X_test = sc.transform(X_test)

# Importing the Keras libraries and packages
import keras
from keras.models import Sequential
from keras.layers import Dense
from keras.models import model_from_json

# Initialising the ANN
classifier = Sequential()

# Adding the input layer and the first hidden layer
classifier.add(Dense(output_dim = 6, init = 'uniform', activation = 'relu', input_dim = 3))

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
json_file = open('model.json', 'r')
loaded_model_json = json_file.read()
json_file.close()
loaded_model = model_from_json(loaded_model_json)
# load weights into new model
loaded_model.load_weights("model.h5")
print("Loaded model from disk")

# Part 3 - Making the predictions and evaluating the model

# Predicting the Test set results
y_pred = loaded_model.predict(X_test)
print(y_pred)
y_pred = (y_pred > 0.5)

# Making the Confusion Matrix
from sklearn.metrics import confusion_matrix
cm = confusion_matrix(y_test, y_pred)
print(cm)
#!/bin/bash

source ../conf/install_framework.cfg

mongo_path=$MONGO_FRAMEWORKDUMP_PATH
log_path=$MONGO_LOG_PATH

echo "`date +"%d-%m-%Y %T"` [INFO] Mongo dump started" > $log_path
mongorestore  --gzip --db framework --drop --archive=$mongo_path/framework.archive
echo "`date +"%d-%m-%Y %T"` [INFO] Mongo dump completed" >> $log_path
sleep 5

#!/bin/bash

source /home/mowgli/install_scripts/conf/install_framework.cfg

warfile_path=$FRAMEWORK_INSTALL_PATH
tomcat_path=$TOMCAT_INSTALL_PATH
log_path=$LOG_FILE_PATH
spark_master=$SPARK_MASTER_URL

echo $SPARK_MASTER_URL
echo $spark_master
echo $tomcat_path

sudo sed -i "s/local\[\*\]/$SPARK_MASTER_URL/g"  $tomcat_path/webapps/framework/WEB-INF/framework-servlet.xml

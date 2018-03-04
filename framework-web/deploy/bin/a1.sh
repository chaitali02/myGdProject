#!/bin/bash

source /home/mowgli/install_scripts/conf/install_framework.cfg

warfile_path=$FRAMEWORK_INSTALL_PATH
tomcat_path=$TOMCAT_INSTALL_PATH
log_path=$LOG_FILE_PATH
spark_master=$SPARK_MASTER_URL

echo `sudo $tomcat_path/bin/startup.sh`
echo "hello"
echo `sudo $tomcat_path/bin/shutdown.sh`

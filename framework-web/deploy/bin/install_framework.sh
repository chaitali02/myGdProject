#!/bin/bash

source ../conf/install_framework.cfg

warfile_path=$FRAMEWORK_INSTALL_PATH
tomcat_path=$TOMCAT_INSTALL_PATH
log_path=$LOG_FILE_PATH
spark_master=$SPARK_MASTER_URL
spark_home=$SPARK_INSTALL_PATH

	stop_Tomcat ()
	{
 		echo "shutting down......"
 		echo `sudo $tomcat_path/bin/shutdown.sh`
		echo "`date +"%d-%m-%Y %T"` [INFO] Shutting down the TOMCAT server" >> $log_path
	}

	start_Tomcat ()
	{
 		echo "starting......"
	 	echo `sudo $tomcat_path/bin/startup.sh`
		echo "`date +"%d-%m-%Y %T"` [INFO] Starting TOMCAT server" >> $log_path
		sleep 40
	}

	start_Spark ()
	{
		echo `sudo $spark_home/sbin/start-master.sh`
		echo "`date +"%d-%m-%Y %T"` [INFO] Starting the Spark master" >> $log_path
		sleep 5
		echo `sudo $spark_home/sbin/start-slaves.sh --master $spark_master`
		echo "`date +"%d-%m-%Y %T"` [INFO] Starting Spark slaves" >> $log_path
	}

	#stop_Spark ()
	#{
	#	echo `sudo $spark_home/sbin/stop-master.sh`
	#	sleep 5
	#	echo `sudo $spark_home/sbin/stop-slaves.sh --master $spark_master`
	#}

	restart ()
	{
 		stop_Tomcat
	 	sleep 10
		echo "`date +"%d-%m-%Y %T"` [INFO] Starting deployment.." >> $log_path
	 	start_Tomcat
 		sleep 20
	}

	#tomcat
	if [ -f "$tomcat_path/webapps/framework.war" ]
	then 
		sudo mv $tomcat_path/webapps/framework.war /tmp
		sudo rm -r $tomcat_path/webapps/framework
		sudo cp $warfile_path/framework.war $tomcat_path/webapps/
		echo "`date +"%d-%m-%Y %T"` [INFO] Starting Framework installation process." > $log_path
		echo "`date +"%d-%m-%Y %T"` [INFO] Deleted old .war file and copied new .war file" >>  $log_path
		sleep 5
		start_Tomcat
		stop_Tomcat
		sudo sed -i "s~local\[\*\]~$SPARK_MASTER_URL~g" $tomcat_path/webapps/framework/WEB-INF/framework-servlet.xml
		start_Tomcat
		if [ "${spark_master}" == "local" ] || [ "${spark_master}" == "local[*]" ]
		then
			exit 1
		else
			sudo rm -r metastore_db
                        sudo rm derby.log
                        start_Spark
		fi
	else
		sudo cp $warfile_path/framework.war $tomcat_path/webapps/
		echo "`date +"%d-%m-%Y %T"` [INFO] Starting Framework installation process." > $log_path
		echo "`date +"%d-%m-%Y %T"` [INFO] Copied new .war file" >> $log_path
		sleep 5
		start_Tomcat
                stop_Tomcat
		sudo sed -i "s~local\[\*\]~$\SPARK_MASTER_URL~g" $tomcat_path/webapps/framework/WEB-INF/framework-servlet.xml
                start_Tomcat
		if [ "${spark_master}" == "local" ] || [ "${spark_master}" == "local[*]" ]
                then
                        exit 1
                else
                        sudo rm -r metastore_db
                        sudo rm derby.log
                        start_Spark
                fi
	fi

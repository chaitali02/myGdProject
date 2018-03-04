set -x

date
cd /home/inferyx

# Setting variables
TOMCAT_HOME=/opt/tomcat
GIT_HOME=~/git

echo "Stopping Tomcat..."
$TOMCAT_HOME/bin/shutdown.sh
kill -9 `ps -ef | grep tomcat | grep inferyx | grep -v "grep tomcat" | awk '{print $2}'`
sleep 5

echo
echo "Starting Tomcat..."
$TOMCAT_HOME/bin/startup.sh

echo "All Done...Enjoy !!!"
cd
date

tail -f $TOMCAT_HOME/logs/catalina.out

#set -x

date
cd /home/inferyx

# Setting variables
export TOMCAT_HOME=/opt/tomcat
export GIT_HOME=~/git
export HOSTNAME=`hostname -a`

#export GIT_SSL_NO_VERIFY=true

echo "Stopping Tomcat..."
$TOMCAT_HOME/bin/shutdown.sh
pkill -f "tomcat"

echo
echo "Backing up framework folder and war..."
mv $TOMCAT_HOME/webapps/framework /tmp/framework.bkp
mv $TOMCAT_HOME/webapps/framework.war /tmp/framework.war.bkp

echo
echo "Pulling master branch..."
cd $GIT_HOME/inferyx
git stash
git pull

echo
echo "Copying config files..."
cp framework-web/deploy/conf/$HOSTNAME/pom.xml .
cp framework-web/deploy/conf/$HOSTNAME/core-site.xml framework-web/src/main/resources
cp framework-web/deploy/conf/$HOSTNAME/hdfs-site.xml framework-web/src/main/resources
cp framework-web/deploy/conf/$HOSTNAME/hive-site.xml framework-web/src/main/resources
cp framework-web/deploy/conf/$HOSTNAME/yarn-site.xml framework-web/src/main/resources
cp framework-web/deploy/conf/$HOSTNAME/log4j.properties framework-web/src/main/resources
cp framework-web/deploy/conf/$HOSTNAME/framework.properties framework-web/src/main/resources
cp framework-web/deploy/conf/$HOSTNAME/framework_ui.properties framework-web/src/main/webapp/app/resources/framework.properties

echo
echo "Building and Deploying war..."
mvn clean install
cp framework-web/target/framework.war $TOMCAT_HOME/webapps
cp framework-web/target/framework/WEB-INF/lib/framework-*.jar $TOMCAT_HOME/shared_lib

echo
echo "Taking mongodb backup..."
mongodump --db framework -o /home/inferyx/mongodump/
mv /home/inferyx/mongodump/framework /home/inferyx/mongodump/framework.$(date +%F_%R)

echo
echo "Refreshing metadata..."
cd $GIT_HOME/inferyx/framework-web/scripts
./run.sh

echo
echo "Starting Tomcat..."
$TOMCAT_HOME/bin/startup.sh

echo "All Done...Enjoy !!!"
cd
date

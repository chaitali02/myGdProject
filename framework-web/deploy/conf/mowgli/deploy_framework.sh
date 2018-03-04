#set -x

date
cd /home/inferyx

# Setting variables
export TOMCAT_HOME=/opt/tomcat
export GIT_HOME=~/git
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
cd $GIT_HOME/projectX
git stash
git pull

echo
echo "Copying config files..."
cp deploy/conf/mowgli/pom.xml .
cp deploy/conf/mowgli/core-site.xml src/main/resources
cp deploy/conf/mowgli/hdfs-site.xml src/main/resources
cp deploy/conf/mowgli/hive-site.xml src/main/resources
cp deploy/conf/mowgli/yarn-site.xml src/main/resources
cp deploy/conf/mowgli/log4j.properties src/main/resources
cp deploy/conf/mowgli/framework.properties src/main/resources
cp deploy/conf/mowgli/framework_ui.properties src/main/webapp/app/resources/framework.properties

echo
echo "Building and Deploying war..."
mvn clean install
cp target/framework.war $TOMCAT_HOME/webapps

echo 
echo "Taking mongodb backup..."
mongodump --db framework -o /home/inferyx/mongodump/
mv /home/inferyx/mongodump/framework /home/inferyx/mongodump/framework.$(date +%F_%R)

echo
echo "Refreshing metadata..."
cd $GIT_HOME/projectX/scripts
./run.sh

echo
echo "Starting Tomcat..."
$TOMCAT_HOME/bin/startup.sh

echo "All Done...Enjoy !!!"
cd
date
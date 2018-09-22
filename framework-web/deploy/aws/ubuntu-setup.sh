ssh -i /data/aws/Ubuntu-VDI.pem ubuntu@34.229.185.111
python3 -c "import sysconfig"
sudo apt install python3-distutils
curl -O https://bootstrap.pypa.io/get-pip.py
python3 get-pip.py --user
sudo apt install awscli
aws configure
aws s3 cp s3://inferyx/tomcat_09212018.tar.gz /backup
sudo apt install openjdk-8-jdk
sudo chown -R inferyx:inferyx /opt/tomcat/
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 9DA31620334BD75D9DCB49F368818C72E52529D4
sudo touch /etc/apt/sources.list.d/mongodb-org-4.0.list
sudo apt-get update
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
sudo bash -c 'echo "deb http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.2 multiverse" > /etc/apt/sources.list.d/mongodb-org-3.2.list'
sudo apt-get update
sudo apt install mongodb-org
sudo mkdir -p /data/db /install /app /backup
sudo chmod 777 /data
sudo chmod 777 /backup
sudo chmod 777 /install
sudo chown inferyx:inferyx /data/db
sudo mkdir -p /user/hive/warehouse/framework
sudo chown inferyx:inferyx /user/hive/warehouse/framework
wget https://archive.apache.org/dist/spark/spark-2.2.0/spark-2.2.0-bin-hadoop2.7.tgz
aws s3 cp s3://inferyx/app-scripts_09212018.tar.gz /backup
cp -r /backup/app /home/inferyx
cp -r /backup/scripts /home/inferyx
cd /home/inferyx/scripts
nohup mongod &
./run.sh
pkill -f "tomcat" ; /opt/tomcat/bin/startup.sh ; tail -100f /opt/tomcat/logs/catalina.out
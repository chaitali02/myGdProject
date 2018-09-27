pip install awscli 
curl -O https://bootstrap.pypa.io/get-pip.py
python get-pip.py --user
rm /opt/tomcat/logs/*
tar -zcvf /backup/tomcat_09212018.tar.gz /opt/tomcat
aws s3 cp /backup/tomcat_09212018.tar.gz s3://inferyx/
tar -zcvf /backup/app-scripts_09212018.tar.gz app/ scripts/
aws s3 cp /backup/app-scripts_09212018.tar.gz s3://inferyx/



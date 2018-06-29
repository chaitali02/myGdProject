./install_application.sh localhost refresh ../app/admin/meta/
./install_application.sh localhost append ../app/datawarehouse/meta/
./install_application.sh localhost append ../app/aml/meta/
./install_application.sh localhost append ../app/economiccapital/meta/
./update_application.sh localhost datasource d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost meta d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost datapod d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost function d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd

./update_application.sh localhost datasource d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost meta d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost function d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost role d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost privilege d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce

./update_application.sh localhost datasource a93ba7a0-51c9-11e8-9c2d-fa7ae01bbebc
./update_application.sh localhost meta a93ba7a0-51c9-11e8-9c2d-fa7ae01bbebc
./update_application.sh localhost function a93ba7a0-51c9-11e8-9c2d-fa7ae01bbebc
./update_application.sh localhost role a93ba7a0-51c9-11e8-9c2d-fa7ae01bbebc
./update_application.sh localhost privilege a93ba7a0-51c9-11e8-9c2d-fa7ae01bbebc
./update_createdOn.sh localhost

rm -r /user/hive/warehouse/framework/*
cp -r ../app/*/data/parquet/* /user/hive/warehouse/framework
mkdir /user/hive/warehouse/framework/model/script
mkdir /user/hive/warehouse/framework/train
mkdir /user/hive/warehouse/framework/predict
mkdir /user/hive/warehouse/framework/simulate
mkdir /user/hive/warehouse/framework/comment
mkdir /user/hive/warehouse/framework/upload
mkdir /user/hive/warehouse/framework/model
mkdir /user/hive/warehouse/framework/download
mkdir /user/hive/warehouse/framework/import
mkdir /user/hive/warehouse/framework/export
mkdir /user/hive/warehouse/framework/script
mkdir /user/hive/warehouse/framework/script/r
mkdir /user/hive/warehouse/framework/script/py


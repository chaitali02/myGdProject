./install_application.sh localhost refresh ../app/admin/meta
./install_application.sh localhost append ../app/admin/aptiva
./install_application.sh localhost append ../app/admin/dot
./install_application.sh localhost append ../app/admin/mufg
./install_application.sh localhost append ../app/edw/meta
./install_application.sh localhost append ../app/ccfraud/meta
./install_application.sh localhost append ../app/ecocap/meta
./install_application.sh localhost append ../app/aml/meta
./install_application.sh localhost append ../app/predmaint/meta
./install_application.sh localhost append ../app/sanctions/meta
./install_application.sh localhost append ../app/db/meta
./update_createdOn.sh localhost
./create_indexes.sh localhost
rm -r /user/hive/warehouse/framework/*
mkdir -p /user/hive/warehouse/framework/data/
cp -r ../app/*/data/parquet/* /user/hive/warehouse/framework/data/


mkdir /user/hive/warehouse/framework/image
mkdir /user/hive/warehouse/framework/image/logo
mkdir /user/hive/warehouse/framework/train
mkdir /user/hive/warehouse/framework/predict
mkdir /user/hive/warehouse/framework/simulate
mkdir /user/hive/warehouse/framework/comment
mkdir /user/hive/warehouse/framework/upload
mkdir -p /user/hive/warehouse/framework/model/script
mkdir -p /user/hive/warehouse/framework/algorithm/script
mkdir /user/hive/warehouse/framework/download
mkdir /user/hive/warehouse/framework/import
mkdir /user/hive/warehouse/framework/export
mkdir /user/hive/warehouse/framework/script
mkdir /user/hive/warehouse/framework/script/r
mkdir /user/hive/warehouse/framework/script/py
mkdir /user/hive/warehouse/framework/deploy
mkdir /user/hive/warehouse/framework/app

cp -r ../app/*/data/csv/header/* /user/hive/warehouse/framework/upload/
cp -r ../deploy/* /user/hive/warehouse/framework/deploy/
cp -r ../app/* /user/hive/warehouse/framework/app/
cp -r ../app/script/model/* /user/hive/warehouse/framework/model/script/
cp -r ../app/script/algorithm/* /user/hive/warehouse/framework/algorithm/script/

echo "All Done...Enjoy !!!"


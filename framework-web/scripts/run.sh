./install_application.sh localhost refresh ../app/admin/meta/
./install_application.sh localhost append ../app/datawarehouse/meta/
./update_application.sh localhost datasource d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost meta d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost datapod d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost function d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_createdOn.sh localhost

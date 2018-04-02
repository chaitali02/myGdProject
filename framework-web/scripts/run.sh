./install_application.sh localhost refresh ../app/admin/meta/
./install_application.sh localhost append ../app/datawarehouse/meta/
./install_application.sh localhost append ../app/aml/meta/
./install_application.sh localhost append ../app/aml/group/
./update_application.sh localhost datasource d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost meta d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost datapod d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost function d7c11fd7-ec1a-40c7-ba25-7da1e8b730cd
./update_application.sh localhost datasource d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost meta d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost function d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost role d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_application.sh localhost privilege d7c11fd7-ec1a-40c7-ba25-7da1e8b730ce
./update_createdOn.sh localhost




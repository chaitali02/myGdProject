###############################################################################
# Copyright (C) Inferyx Inc, 2018 All rights reserved. 
#
# This unpublished material is proprietary to Inferyx Inc.
# The methods and techniques described herein are considered  trade 
# secrets and/or confidential. Reproduction or distribution, in whole or 
# in part, is forbidden.
#
# Written by Yogesh Palrecha <ypalrecha@inferyx.com>
###############################################################################
hostname=$1
action=$2
metadir=$3

if [[ $action = ""  || $hostname = "" || $metadir = "" ]] ; then
        echo Usage: install_application.sh [hostname] [refresh/append] [metadir]
	exit 1
fi

if [[ $action = "refresh" ]] ; then
	echo Dropping database
	mongo --host $hostname framework -eval "printjson(db.dropDatabase())"
	echo Creating collection
	mongo --quiet --host $hostname framework --eval "db.createCollection(\"activity\");
	db.createCollection(\"algorithm\");
	db.createCollection(\"application\");
	db.createCollection(\"condition\");
	db.createCollection(\"dag\");
	db.createCollection(\"dagexec\");
	db.createCollection(\"dashboard\");
	db.createCollection(\"dq\");
	db.createCollection(\"dqexec\");
	db.createCollection(\"dqgroup\");
	db.createCollection(\"dqgroupexec\");
	db.createCollection(\"datastore\");
	db.createCollection(\"datapod\");
	db.createCollection(\"dataset\");
	db.createCollection(\"datasource\");
	db.createCollection(\"dimension\");
	db.createCollection(\"distribution\");
	db.createCollection(\"expression\");
	db.createCollection(\"filter\");
	db.createCollection(\"formula\");
	db.createCollection(\"function\");
	db.createCollection(\"group\");
	db.createCollection(\"load\");
	db.createCollection(\"loadexec\");
	db.createCollection(\"map\");
	db.createCollection(\"mapexec\");
	db.createCollection(\"measure\");
	db.createCollection(\"model\");
	db.createCollection(\"modelexec\");
	db.createCollection(\"paramlist\");
	db.createCollection(\"paramset\");
	db.createCollection(\"privilege\");
	db.createCollection(\"profile\");
	db.createCollection(\"profileexec\");
	db.createCollection(\"profilegroup\");
	db.createCollection(\"profilegroupexec\");
	db.createCollection(\"relation\");
	db.createCollection(\"role\");
	db.createCollection(\"rule\");
	db.createCollection(\"ruleexec\");
	db.createCollection(\"rulegroup\");
	db.createCollection(\"rulegroupexec\");
	db.createCollection(\"session\");
	db.createCollection(\"user\");
	db.createCollection(\"vizpod\");
	db.createCollection(\"vizexec\");"

fi

for collectionpath in $metadir/*
do
        collection=`basename $collectionpath`

       	#echo Dropping collection $collection
        #mongo --hostname $hostname framework -eval "printjson(db.${collection}.drop())"

        for document in $collectionpath/*
        do
                echo Importing document $document
                paste -s $document > $document.tmp
                mongoimport --host $hostname --db framework --collection $collection --file $document.tmp
                rm $document.tmp
        done

done

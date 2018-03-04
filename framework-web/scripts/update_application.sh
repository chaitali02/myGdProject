#*******************************************************************************
# Copyright (C) GridEdge Consulting LLC, 2016 All rights reserved. 
#
# This unpublished material is proprietary to GridEdge Consulting LLC.
# The methods and techniques described herein are considered  trade 
# secrets and/or confidential. Reproduction or distribution, in whole or 
# in part, is forbidden.
#
# Written by Yogesh Palrecha <ypalrecha@gridedge.com>
#*******************************************************************************
hostname=$1
collection=$2
uuid=$3

if [[ $hostname = "" || $collection = "" || $uuid = "" ]] ; then
        echo Usage: update_application.sh [hostname] [collection] [uuid]
	exit 1
fi

echo Adding application info to $collection collection
if [[ $collection != "datapod" ]] ; then
	/home/joy/Documents/mongodb-linux-x86_64-ubuntu1404-3.4.2/bin/mongo --host $hostname framework --eval "printjson(db.${collection}.updateMany( {}, {\$push: {\"appInfo\":{ \"ref\" : { \"type\" : \"application\", \"uuid\" : \"${uuid}\" } } }}, {multi:true}))"
else
	/home/joy/Documents/mongodb-linux-x86_64-ubuntu1404-3.4.2/bin/mongo --host $hostname framework --eval "printjson(db.${collection}.updateOne( {name:\"dq_rule_results\"}, {\$push: {\"appInfo\":{ \"ref\" : { \"type\" : \"application\", \"uuid\" : \"${uuid}\" } } }}, {}))"
fi

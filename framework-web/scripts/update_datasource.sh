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
collection=datapod
uuid=$2

if [[ $hostname = "" || $collection = "" || $uuid = "" ]] ; then
        echo Usage: update_datasource.sh [hostname] [uuid]
	exit 1
fi

echo Updating datasource in datapod collection
mongo --host $hostname framework --eval "printjson(db.${collection}.updateMany( {}, {\$set: {\"datasource\":{ \"ref\" : { \"uuid\" : \"${uuid}\" } } }}, {multi:true}))"
